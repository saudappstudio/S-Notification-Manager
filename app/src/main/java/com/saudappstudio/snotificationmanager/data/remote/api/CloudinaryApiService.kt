package com.saudappstudio.snotificationmanager.data.remote.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Data model for Cloudinary API upload response.
 */
data class CloudinaryUploadResponseDto(
    @SerializedName("secure_url") val secureUrl: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("public_id") val publicId: String?,
    @SerializedName("format") val format: String?,
    @SerializedName("width") val width: Int?,
    @SerializedName("height") val height: Int?,
    @SerializedName("error") val error: CloudinaryErrorDto?
)

/**
 * Data model for Cloudinary API error response.
 */
data class CloudinaryErrorDto(
    @SerializedName("message") val message: String?
)

/**
 * Retrofit interface for uploading media directly to Cloudinary via REST API.
 */
interface CloudinaryApiService {

    @Multipart
    @POST("v1_1/{cloudName}/image/upload")
    suspend fun uploadImage(
        @Path("cloudName") cloudName: String,
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: RequestBody
    ): Response<CloudinaryUploadResponseDto>
}
