package com.example.moil.feature.auth.module.data.mapper

import com.example.moil.feature.auth.module.data.dto.TokenResponseDto
import com.example.moil.feature.auth.module.data.dto.UserProfileResponseDto
import com.example.moil.feature.auth.module.data.dto.VerificationStepDto
import com.example.moil.feature.auth.module.domain.model.UserProfile
import com.example.moil.feature.auth.module.domain.model.VerificationStep

internal fun UserProfileResponseDto.toDomain(): UserProfile = UserProfile(
    userId = userId,
    name = name,
    email = email,
)

internal fun TokenResponseDto.toUserProfileOrNull(fallbackName: String? = null): UserProfile? {
    val responseUserId = userId ?: return null
    val profileName = name ?: fallbackName ?: return null
    val profileEmail = email ?: return null

    return UserProfile(
        userId = responseUserId,
        name = profileName,
        email = profileEmail,
    )
}

internal fun VerificationStep.toDto(): VerificationStepDto = when (this) {
    VerificationStep.SignUp -> VerificationStepDto.SignUp
    VerificationStep.Reset -> VerificationStepDto.Reset
}
