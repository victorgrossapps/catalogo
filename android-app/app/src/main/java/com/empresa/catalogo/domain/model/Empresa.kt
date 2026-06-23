package com.empresa.catalogo.domain.model

data class Empresa(
    val nombreComercial: String,
    val logoUrl: String?,
    val textoQuienesSomos: String?,
    val telefono: String?,
    val whatsapp: String?,
    val correo: String?,
    val direccion: String?
)
