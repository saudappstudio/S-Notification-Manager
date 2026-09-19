package com.saudappstudio.snotificationmanager.domain.repository

import android.net.Uri

/**
 * Repository contract for uploading media assets to Cloudinary cloud storage.
 */
interface CloudinaryRepository {

    /**
     * Uploads an image from local Uri to Cloudinary and returns the public secure URL.
     *
     * @param imageUri Android Uri of selected image.
     * @param cloudName Cloudinary account cloud name.
     * @param uploadPreset Cloudinary unsigned upload preset.
     * @return Result wrapping the hosted secure image URL string.
     */
    suspend fun uploadImage(imageUri: Uri, cloudName: String, uploadPreset: String): Result<String>
}
