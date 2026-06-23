package com.empresa.catalogo.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.dataStore by preferencesDataStore(name = "catalog_preferences")

data class LocalCatalogState(
    val catalogId: Long?,
    val versionCodigo: String?,
    val versionNumero: Int,
    val checksum: String?,
    val localPath: String?,
    val downloadedAt: Long?,
    val deviceUuid: String
)

class CatalogPreferences(private val context: Context) {
    private object Keys {
        val CATALOG_ID = longPreferencesKey("catalog_id")
        val VERSION_CODIGO = stringPreferencesKey("version_codigo")
        val VERSION_NUMERO = intPreferencesKey("version_numero")
        val CHECKSUM = stringPreferencesKey("checksum")
        val LOCAL_PATH = stringPreferencesKey("local_path")
        val DOWNLOADED_AT = longPreferencesKey("downloaded_at")
        val DEVICE_UUID = stringPreferencesKey("device_uuid")
    }

    val state: Flow<LocalCatalogState> = context.dataStore.data.map { prefs ->
        val uuid = prefs[Keys.DEVICE_UUID] ?: UUID.randomUUID().toString()
        LocalCatalogState(
            catalogId = prefs[Keys.CATALOG_ID],
            versionCodigo = prefs[Keys.VERSION_CODIGO],
            versionNumero = prefs[Keys.VERSION_NUMERO] ?: 0,
            checksum = prefs[Keys.CHECKSUM],
            localPath = prefs[Keys.LOCAL_PATH],
            downloadedAt = prefs[Keys.DOWNLOADED_AT],
            deviceUuid = uuid
        )
    }

    suspend fun ensureDeviceUuid(): String {
        var uuid: String? = null
        context.dataStore.edit { prefs ->
            uuid = prefs[Keys.DEVICE_UUID] ?: UUID.randomUUID().toString().also {
                prefs[Keys.DEVICE_UUID] = it
            }
        }
        return requireNotNull(uuid)
    }

    suspend fun saveCatalog(
        catalogId: Long,
        versionCodigo: String,
        versionNumero: Int,
        checksum: String,
        localPath: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CATALOG_ID] = catalogId
            prefs[Keys.VERSION_CODIGO] = versionCodigo
            prefs[Keys.VERSION_NUMERO] = versionNumero
            prefs[Keys.CHECKSUM] = checksum
            prefs[Keys.LOCAL_PATH] = localPath
            prefs[Keys.DOWNLOADED_AT] = System.currentTimeMillis()
        }
    }
}
