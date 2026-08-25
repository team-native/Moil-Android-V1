package com.example.moil.feature.auth.module.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponseDto(
    @SerialName("userId") val userId: Long,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
)

@Serializable
data class VerificationResponseDto(
    @SerialName("verifyId") val verifyId: String,
)

@Serializable
data class VerifiedSessionResponseDto(
    @SerialName("sessionId") val sessionId: String,
)

@Serializable
data class TokenResponseDto(
    @SerialName("userId") val userId: Long? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
    @SerialName("tokenType") val tokenType: String? = null,
    @SerialName("expiresIn") val expiresIn: Long? = null,
)
