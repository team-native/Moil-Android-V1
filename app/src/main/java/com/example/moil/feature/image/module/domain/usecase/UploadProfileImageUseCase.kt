package com.example.moil.feature.image.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.image.module.domain.model.UploadedProfileImage
import com.example.moil.feature.image.module.domain.repository.ImageRepository
import javax.inject.Inject

class UploadProfileImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
) {
    // 이미지 선택 결과인 content URI를 서버에 업로드하고 반환된 경로를 제공합니다.
    suspend operator fun invoke(contentUri: String): MoilResult<UploadedProfileImage> {
        if (contentUri.isBlank()) {
            return MoilResult.Failure(com.example.moil.core.domain.MoilError.Network)
        }

        return imageRepository.uploadProfileImage(contentUri)
    }
}
