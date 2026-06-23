package com.empresa.catalogo.domain.model

data class Catalogo(
    val id: Long,
    val titulo: String,
    val descripcion: String?,
    val versionCodigo: String,
    val versionNumero: Int,
    val tipo: String,
    val archivoUrl: String,
    val portadaUrl: String?,
    val pesoBytes: Long,
    val checksum: String,
    val obligatorio: Boolean,
    val mensajeActualizacion: String?,
    val publicadoEn: String?
)
