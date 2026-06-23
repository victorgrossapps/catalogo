package com.empresa.catalogo.data.mapper

import com.empresa.catalogo.data.remote.dto.CatalogoDto
import com.empresa.catalogo.data.remote.dto.EmpresaDto
import com.empresa.catalogo.domain.model.Catalogo
import com.empresa.catalogo.domain.model.Empresa

fun CatalogoDto.toDomain(): Catalogo = Catalogo(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    versionCodigo = versionCodigo,
    versionNumero = versionNumero,
    tipo = tipo,
    archivoUrl = archivoUrl,
    portadaUrl = portadaUrl,
    pesoBytes = pesoBytes,
    checksum = checksum,
    obligatorio = obligatorio,
    mensajeActualizacion = mensajeActualizacion,
    publicadoEn = publicadoEn
)

fun EmpresaDto.toDomain(): Empresa = Empresa(
    nombreComercial = nombreComercial,
    logoUrl = logoUrl,
    textoQuienesSomos = textoQuienesSomos,
    telefono = telefono,
    whatsapp = whatsapp,
    correo = correo,
    direccion = direccion
)
