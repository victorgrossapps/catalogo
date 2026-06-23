package com.empresa.catalogo.data.repository

import android.os.Build
import com.empresa.catalogo.BuildConfig
import com.empresa.catalogo.data.file.CatalogFileManager
import com.empresa.catalogo.data.local.CatalogPreferences
import com.empresa.catalogo.data.local.LocalCatalogState
import com.empresa.catalogo.data.mapper.toDomain
import com.empresa.catalogo.data.remote.CatalogApiService
import com.empresa.catalogo.data.remote.dto.DeviceSyncDto
import com.empresa.catalogo.data.remote.dto.DownloadEventDto
import com.empresa.catalogo.domain.model.Catalogo
import com.empresa.catalogo.domain.model.Empresa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException

sealed interface UpdateResult {
    data class UpToDate(val local: LocalCatalogState) : UpdateResult
    data class Available(val catalogo: Catalogo, val local: LocalCatalogState) : UpdateResult
    data class Downloaded(val catalogo: Catalogo, val localPath: String) : UpdateResult
    data class NoLocalCatalog(val message: String) : UpdateResult
}

sealed interface ApiConnectionResult {
    data class Success(
        val service: String?,
        val timestamp: String?,
        val url: String
    ) : ApiConnectionResult

    data class Error(
        val url: String,
        val statusCode: Int?,
        val message: String
    ) : ApiConnectionResult
}

class CatalogDownloadException(message: String) : Exception(message)

class CatalogRepository(
    private val api: CatalogApiService,
    private val preferences: CatalogPreferences,
    private val files: CatalogFileManager
) {
    val localState: Flow<LocalCatalogState> = preferences.state

    suspend fun getEmpresa(): Empresa? =
        api.getEmpresa().empresa?.toDomain()

    suspend fun testApiConnection(): ApiConnectionResult {
        return try {
            val response = api.health()
            val body = response.body()

            if (response.isSuccessful && body?.ok == true) {
                ApiConnectionResult.Success(
                    service = body.service,
                    timestamp = body.timestamp,
                    url = BuildConfig.API_BASE_URL
                )
            } else {
                ApiConnectionResult.Error(
                    url = BuildConfig.API_BASE_URL,
                    statusCode = response.code(),
                    message = body?.service ?: response.message().ifBlank { "Respuesta no válida de la API." }
                )
            }
        } catch (error: IOException) {
            ApiConnectionResult.Error(
                url = BuildConfig.API_BASE_URL,
                statusCode = null,
                message = error.message ?: "No se pudo conectar con la API."
            )
        } catch (error: Throwable) {
            ApiConnectionResult.Error(
                url = BuildConfig.API_BASE_URL,
                statusCode = null,
                message = error.message ?: "Error inesperado al probar la conexión API."
            )
        }
    }

    suspend fun checkForUpdate(local: LocalCatalogState): UpdateResult {
        val response = api.getCatalogoActual()

        if (response.code() == 404) {
            return UpdateResult.NoLocalCatalog("Conexión API correcta. No hay catálogo activo publicado.")
        }

        if (!response.isSuccessful) {
            return UpdateResult.NoLocalCatalog("La API respondió con error HTTP ${response.code()} al consultar el catálogo.")
        }

        val remote = response.body()?.catalogo?.toDomain()
            ?: return UpdateResult.NoLocalCatalog("Conexión API correcta. No hay catálogo activo publicado.")

        return if (remote.versionNumero > local.versionNumero) {
            UpdateResult.Available(remote, local)
        } else {
            UpdateResult.UpToDate(local)
        }
    }

    suspend fun downloadCatalog(catalogo: Catalogo, onProgress: (Float) -> Unit): UpdateResult.Downloaded {
        val deviceUuid = preferences.ensureDeviceUuid()

        return try {
            val downloaded = withContext(Dispatchers.IO) {
                val response = api.downloadFile(catalogo.archivoUrl)
                if (!response.isSuccessful) {
                    throw CatalogDownloadException(
                        "Error descargando PDF. HTTP ${response.code()} ${response.message()}. URL: ${catalogo.archivoUrl}"
                    )
                }

                val contentType = response.headers()["Content-Type"].orEmpty()
                if (contentType.isNotBlank() && !contentType.contains("pdf", ignoreCase = true)) {
                    throw CatalogDownloadException(
                        "Contenido inválido al descargar PDF. Content-Type: $contentType. URL: ${catalogo.archivoUrl}"
                    )
                }

                val body = response.body() ?: throw CatalogDownloadException(
                    "La respuesta de descarga está vacía. URL: ${catalogo.archivoUrl}"
                )
                files.writePending(body) { progress ->
                    onProgress(progress)
                }
                try {
                    files.validatePending(catalogo)
                } catch (error: Throwable) {
                    throw CatalogDownloadException("Validación de PDF fallida. ${error.message} URL: ${catalogo.archivoUrl}")
                }
                val active = files.promotePending()

                preferences.saveCatalog(
                    catalogId = catalogo.id,
                    versionCodigo = catalogo.versionCodigo,
                    versionNumero = catalogo.versionNumero,
                    checksum = catalogo.checksum,
                    localPath = active.absolutePath
                )

                UpdateResult.Downloaded(catalogo, active.absolutePath)
            }

            withContext(Dispatchers.IO) {
                api.registerDownload(
                    DownloadEventDto(
                        catalogoId = catalogo.id,
                        deviceUuid = deviceUuid,
                        nombreDispositivo = Build.MODEL,
                        versionApp = BuildConfig.VERSION_NAME,
                        versionCatalogo = catalogo.versionCodigo,
                        estado = "exitoso"
                    )
                )

                api.syncDevice(
                    DeviceSyncDto(
                        deviceUuid = deviceUuid,
                        nombreDispositivo = Build.MODEL,
                        versionApp = BuildConfig.VERSION_NAME,
                        ultimoCatalogoVersion = catalogo.versionCodigo
                    )
                )
            }

            downloaded
        } catch (error: Throwable) {
            withContext(Dispatchers.IO) {
                files.discardPending()
            }
            runCatching { withContext(Dispatchers.IO) {
                api.registerDownload(
                    DownloadEventDto(
                        catalogoId = catalogo.id,
                        deviceUuid = preferences.ensureDeviceUuid(),
                        nombreDispositivo = Build.MODEL,
                        versionApp = BuildConfig.VERSION_NAME,
                        versionCatalogo = catalogo.versionCodigo,
                        estado = "fallido",
                        mensajeError = error.message
                    )
                )
            } }
            throw if (error is CatalogDownloadException) {
                error
            } else {
                CatalogDownloadException(
                    "No fue posible descargar el catálogo. ${error.message ?: error::class.java.simpleName}. URL: ${catalogo.archivoUrl}"
                )
            }
        }
    }

    fun hasActiveCatalog(local: LocalCatalogState): Boolean =
        files.hasActiveCatalog(local.localPath)
}
