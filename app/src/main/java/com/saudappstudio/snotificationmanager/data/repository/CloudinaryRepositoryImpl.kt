package com.saudappstudio.snotificationmanager.data.repository

import android.content.Context
import android.net.Uri
import com.saudappstudio.snotificationmanager.core.logging.Logger
import com.saudappstudio.snotificationmanager.data.remote.api.CloudinaryApiService
import com.saudappstudio.snotificationmanager.domain.repository.CloudinaryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for uploading image files to Cloudinary using OkHttp/Retrofit multipart requests.
 */
@Singleton
class CloudinaryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cloudinaryApiService: CloudinaryApiService
) : CloudinaryRepository {

    override suspend fun uploadImage(
        imageUri: Uri,
        cloudName: String,
        uploadPreset: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(imageUri) ?: "image/jpeg"
            
            val inputStream = contentResolver.openInputStream(imageUri)
                ?: return@withContext Result.failure(Exception("Unable to open image stream"))

            val bytes = inputStream.use { it.readBytes() }
            if (bytes.isEmpty()) {
                return@withContext Result.failure(Exception("Selected image file is empty"))
            }

            val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", "upload_${System.currentTimeMillis()}.jpg", requestFile)
            val presetBody = uploadPreset.toRequestBody("text/plain".toMediaTypeOrNull())

            val targetCloud = cloudName.ifBlank { "dvyx3z9vp" }
            val response = cloudinaryApiService.uploadImage(targetCloud, filePart, presetBody)

            if (response.isSuccessful) {
                val body = response.body()
                val secureUrl = body?.secureUrl ?: body?.url
                if (!secureUrl.isNullOrBlank()) {
                    Logger.i("Cloudinary upload successful: $secureUrl")
                    Result.success(secureUrl)
                } else {
                    val errMsg = body?.error?.message ?: "Upload succeeded but no URL was returned"
                    Logger.e("Cloudinary error: $errMsg")
                    Result.failure(Exception(errMsg))
                }
            } else {
                val errorStr = response.errorBody()?.string() ?: "Cloudinary HTTP ${response.code()}"
                Logger.e("Cloudinary upload failed: $errorStr")
                Result.failure(Exception(errorStr))
            }
        } catch (e: Exception) {
            Logger.e("Exception during Cloudinary upload", e)
            Result.failure(e)
        }
    }
}
