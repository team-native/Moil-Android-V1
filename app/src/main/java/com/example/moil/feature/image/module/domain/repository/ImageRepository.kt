package com.example.moil.feature.image.module.domain.repository

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.image.module.domain.model.UploadedProfileImage

interface ImageRepository {
    suspend fun uploadProfileImage(contentUri: String): MoilResult<UploadedProfileImage>
}
