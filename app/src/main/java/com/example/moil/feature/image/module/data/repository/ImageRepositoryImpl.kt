package com.example.moil.feature.image.module.data.repository

import com.example.moil.core.domain.MoilResult
import com.example.moil.core.domain.mapToDomain
import com.example.moil.feature.image.module.data.remote.ImageRemoteDataSource
import com.example.moil.feature.image.module.domain.model.UploadedProfileImage
import com.example.moil.feature.image.module.domain.repository.ImageRepository
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageRemoteDataSource: ImageRemoteDataSource,
) : ImageRepository {
    override suspend fun uploadProfileImage(contentUri: String): MoilResult<UploadedProfileImage> = imageRemoteDataSource
        .uploadProfileImage(contentUri)
        .mapToDomain { response -> UploadedProfileImage(imagePath = response.imagePath) }
}
