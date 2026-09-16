package com.saudappstudio.snotificationmanager.data.remote.api

import com.saudappstudio.snotificationmanager.data.remote.models.BackendHealthDto
import com.saudappstudio.snotificationmanager.data.remote.models.NotificationRequestDto
import com.saudappstudio.snotificationmanager.data.remote.models.NotificationResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit interface communicating with the Netlify serverless notification backend.
 */
interface NetlifyApiService {

    @POST("send-notification")
    suspend fun sendNotification(
        @Body request: NotificationRequestDto
    ): Response<NotificationResponseDto>

    @POST("test-notification")
    suspend fun sendTestNotification(
        @Body request: NotificationRequestDto
    ): Response<NotificationResponseDto>

    @GET("health")
    suspend fun checkHealth(): Response<BackendHealthDto>
}
