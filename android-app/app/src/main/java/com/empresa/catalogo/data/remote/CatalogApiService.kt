package com.empresa.catalogo.data.remote

import com.empresa.catalogo.data.remote.dto.CatalogoActualResponseDto
import com.empresa.catalogo.data.remote.dto.DeviceSyncDto
import com.empresa.catalogo.data.remote.dto.DownloadEventDto
import com.empresa.catalogo.data.remote.dto.EmpresaResponseDto
import com.empresa.catalogo.data.remote.dto.HealthResponseDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming
import retrofit2.http.Url

interface CatalogApiService {
    @GET("health")
    suspend fun health(): Response<HealthResponseDto>

    @GET("catalogo/actual")
    suspend fun getCatalogoActual(): Response<CatalogoActualResponseDto>

    @GET("empresa")
    suspend fun getEmpresa(): EmpresaResponseDto

    @Streaming
    @GET
    suspend fun downloadFile(@Url url: String): Response<ResponseBody>

    @POST("catalogo/descarga")
    suspend fun registerDownload(@Body event: DownloadEventDto)

    @POST("dispositivos/sync")
    suspend fun syncDevice(@Body event: DeviceSyncDto)
}
