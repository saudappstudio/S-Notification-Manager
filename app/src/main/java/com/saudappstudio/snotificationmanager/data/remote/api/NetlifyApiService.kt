package com.saudappstudio.snotificationmanager.data.remote.api

import com.saudappstudio.snotificationmanager.data.remote.models.AnalyticsResponseDto
import com.saudappstudio.snotificationmanager.data.remote.models.BackendHealthDto
import com.saudappstudio.snotificationmanager.data.remote.models.CrashlyticsFetchResponseDto
import com.saudappstudio.snotificationmanager.data.remote.models.NotificationRequestDto
import com.saudappstudio.snotificationmanager.data.remote.models.NotificationResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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

    @GET("analytics")
    suspend fun getAnalyticsData(
        @Query("backendKey") backendKey: String,
        @Query("propertyId") propertyId: String? = null,
        @Query("timeRange") timeRange: String = "7D"
    ): Response<AnalyticsResponseDto>

    @GET("fetch-crashlytics")
    suspend fun fetchCrashlytics(
        @Query("backendKey") backendKey: String = "dictionary",
        @Query("appId") appId: String = ""
    ): Response<CrashlyticsFetchResponseDto>
}
