package com.empresa.catalogo.data.remote.dto

import com.squareup.moshi.Json

data class CatalogoActualResponseDto(
    val ok: Boolean,
    val catalogo: CatalogoDto?
)

data class CatalogoDto(
    val id: Long,
    val titulo: String,
    val descripcion: String?,
    @Json(name = "version_codigo") val versionCodigo: String,
    @Json(name = "version_numero") val versionNumero: Int,
    val tipo: String,
    @Json(name = "archivo_url") val archivoUrl: String,
    @Json(name = "portada_url") val portadaUrl: String?,
    @Json(name = "peso_bytes") val pesoBytes: Long,
    val checksum: String,
    val obligatorio: Boolean,
    @Json(name = "mensaje_actualizacion") val mensajeActualizacion: String?,
    @Json(name = "publicado_en") val publicadoEn: String?
)

data class EmpresaResponseDto(
    val ok: Boolean,
    val empresa: EmpresaDto?
)

data class EmpresaDto(
    @Json(name = "nombre_comercial") val nombreComercial: String,
    @Json(name = "logo_url") val logoUrl: String?,
    @Json(name = "texto_quienes_somos") val textoQuienesSomos: String?,
    val telefono: String?,
    val whatsapp: String?,
    val correo: String?,
    val direccion: String?
)

data class DownloadEventDto(
    @Json(name = "catalogo_id") val catalogoId: Long,
    @Json(name = "device_uuid") val deviceUuid: String,
    @Json(name = "nombre_dispositivo") val nombreDispositivo: String?,
    @Json(name = "version_app") val versionApp: String,
    @Json(name = "version_catalogo") val versionCatalogo: String,
    val estado: String,
    @Json(name = "mensaje_error") val mensajeError: String? = null
)

data class DeviceSyncDto(
    @Json(name = "device_uuid") val deviceUuid: String,
    @Json(name = "nombre_dispositivo") val nombreDispositivo: String?,
    @Json(name = "version_app") val versionApp: String,
    @Json(name = "ultimo_catalogo_version") val ultimoCatalogoVersion: String?
)
