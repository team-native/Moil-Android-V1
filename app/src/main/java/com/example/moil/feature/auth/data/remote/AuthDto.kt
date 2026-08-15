package com.example.moil.feature.auth.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class VerificationStepDto {
    @SerialName("SIGNUP")
    SignUp,
    @SerialName("RESET")
    Reset,
}

@Serializable
data class SendCodeRequestDto(
    @SerialName("name") val name: String? = null,
    @SerialName("email") val email: String,
    @SerialName("step") val step: VerificationStepDto,
)

@Serializable
data class VerifyCodeRequestDto(
    @SerialName("verifyId") val verifyId: String,
    @SerialName("code") val code: String,
)

@Serializable
data class PasswordSessionRequestDto(
    @SerialName("sessionId") val sessionId: String,
    @SerialName("password") val password: String,
    @SerialName("pwd") val passwordConfirmation: String,
)

@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
)

@Serializable
data class ChangePasswordRequestDto(
    @SerialName("origin") val origin: String,
    @SerialName("newpwd") val newPassword: String,
    @SerialName("checkpwd") val passwordConfirmation: String,
)

@Serializable
data class RefreshTokenRequestDto(
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class DeleteAccountRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("leftData") val leaveData: Boolean,
)

@Serializable
data class UpdateProfileRequestDto(
    @SerialName("name") val name: String,
)

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
