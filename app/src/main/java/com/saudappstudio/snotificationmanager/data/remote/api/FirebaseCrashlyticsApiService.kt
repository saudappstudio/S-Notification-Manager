package com.saudappstudio.snotificationmanager.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Data Transfer Objects for Firebase Crashlytics API responses.
 */
data class FirebaseCrashlyticsIssueDto(
    val issueId: String,
    val title: String,
    val subtitle: String,
    val crashCount: Int,
    val impactUsers: Int,
    val isFatal: Boolean,
    val state: String,
    val firstSeenTime: String?,
    val lastSeenTime: String?
)

data class FirebaseCrashlyticsIssuesResponse(
    val issues: List<FirebaseCrashlyticsIssueDto>?
)

/**
 * Retrofit interface for Firebase Crashlytics REST API operations.
 */
interface FirebaseCrashlyticsApiService {

    @GET("v1alpha1/projects/{projectId}/apps/{appId}/issues")
    suspend fun getAppIssues(
        @Path("projectId") projectId: String,
        @Path("appId") appId: String,
        @Header("Authorization") bearerToken: String,
        @Query("pageSize") pageSize: Int = 50
    ): Response<FirebaseCrashlyticsIssuesResponse>
}
