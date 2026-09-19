package com.saudappstudio.snotificationmanager.data.remote.api

import com.saudappstudio.snotificationmanager.core.datastore.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that attaches the Bearer API authentication secret to outbound requests.
 */
class AuthInterceptor(
    private val preferencesManager: PreferencesManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Do not attach app Auth token or JSON content-type to external APIs such as Cloudinary
        if (originalRequest.url.host.contains("cloudinary.com")) {
            return chain.proceed(originalRequest)
        }

        val userPrefs = runBlocking { preferencesManager.userPreferencesFlow.first() }
        val token = userPrefs.apiToken

        val newRequestBuilder = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")

        if (token.isNotBlank()) {
            newRequestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(newRequestBuilder.build())
    }
}
