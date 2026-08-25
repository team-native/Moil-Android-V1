package com.example.moil.feature.auth.module.domain.model

enum class VerificationStep { SignUp, Reset }

data class Verification(val verifyId: String)
data class VerifiedSession(val sessionId: String)
data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val profile: UserProfile? = null,
)

data class UserProfile(
    val userId: Long,
    val name: String,
    val email: String,
)
