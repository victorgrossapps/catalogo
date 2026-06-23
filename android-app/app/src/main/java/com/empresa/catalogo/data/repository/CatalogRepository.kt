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
import kotlinx.coroutines.flow.Flow

sealed interface UpdateResult {
    data class UpToDate(val local: LocalCatalogState) : UpdateResult
    data class Available(val catalogo: Catalogo, val local: LocalCatalogState) : UpdateResult
    data class Downloaded(val catalogo: Catalogo, val localPath: String) : UpdateResult
    data class NoLocalCatalog(val message: String) : UpdateResult
}

class CatalogRepository(
    private val api: CatalogApiService,
    private val preferences: CatalogPreferences,
    private val files: CatalogFileManager
) {
    val localState: Flow<LocalCatalogState> = preferences.state

    suspend fun getEmpresa(): Empresa? =
        api.getEmpresa().empresa?.toDomain()

    suspend fun checkForUpdate(local: LocalCatalogState): UpdateResult {
        val remote = api.getCatalogoActual().catalogo?.toDomain()
            ?: return UpdateResult.NoLocalCatalog("No hay catalogo publicado.")

        return if (remote.versionNumero > local.versionNumero) {
            UpdateResult.Available(remote, local)
        } else {
            UpdateResult.UpToDate(local)
        }
    }

    suspend fun downloadCatalog(catalogo: Catalogo, onProgress: (Float) -> Unit): UpdateResult.Downloaded {
        val deviceUuid = preferences.ensureDeviceUuid()

        try {
            val response = api.downloadFile(catalogo.archivoUrl)
            require(response.isSuccessful) { "No fue posible descargar el catalogo." }

            val body = requireNotNull(response.body()) { "La respuesta de descarga esta vacia." }
            files.writePending(body, onProgress)
            files.validatePending(catalogo)
            val active = files.promotePending()

            preferences.saveCatalog(
                catalogId = catalogo.id,
                versionCodigo = catalogo.versionCodigo,
                versionNumero = catalogo.versionNumero,
                checksum = catalogo.checksum,
                localPath = active.absolutePath
            )

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

            return UpdateResult.Downloaded(catalogo, active.absolutePath)
        } catch (error: Throwable) {
            files.discardPending()
            runCatching {
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
            }
            throw error
        }
    }

    fun hasActiveCatalog(local: LocalCatalogState): Boolean =
        files.hasActiveCatalog(local.localPath)
}
