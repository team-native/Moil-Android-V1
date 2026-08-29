package com.example.moil.feature.image.module.data.remote

import com.example.moil.core.network.NetworkResult
import com.example.moil.feature.image.module.data.dto.ImageUploadResponseDto

interface ImageRemoteDataSource {
    suspend fun uploadProfileImage(contentUri: String): NetworkResult<ImageUploadResponseDto>
}
