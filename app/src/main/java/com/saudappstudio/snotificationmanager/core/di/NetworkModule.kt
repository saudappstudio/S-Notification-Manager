package com.saudappstudio.snotificationmanager.core.di

import android.content.Context
import com.saudappstudio.snotificationmanager.BuildConfig
import com.saudappstudio.snotificationmanager.core.datastore.PreferencesManager
import com.saudappstudio.snotificationmanager.core.logging.Logger
import com.saudappstudio.snotificationmanager.core.security.BiometricAuthManager
import com.saudappstudio.snotificationmanager.data.remote.api.AuthInterceptor
import com.saudappstudio.snotificationmanager.data.remote.api.NetlifyApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager = PreferencesManager(context)

    @Provides
    @Singleton
    fun provideBiometricAuthManager(
        @ApplicationContext context: Context
    ): BiometricAuthManager = BiometricAuthManager(context)

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        preferencesManager: PreferencesManager
    ): AuthInterceptor = AuthInterceptor(preferencesManager)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor { message ->
                Logger.d(message, "NetworkRequest")
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logging)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideNetlifyApiService(
        okHttpClient: OkHttpClient,
        preferencesManager: PreferencesManager
    ): NetlifyApiService {
        // Read initial URL or default
        val userPrefs = runBlocking { preferencesManager.userPreferencesFlow.first() }
        var baseUrl = userPrefs.backendUrl.trim()
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/"
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NetlifyApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCloudinaryApiService(
        okHttpClient: OkHttpClient
    ): com.saudappstudio.snotificationmanager.data.remote.api.CloudinaryApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(com.saudappstudio.snotificationmanager.data.remote.api.CloudinaryApiService::class.java)
    }
}
