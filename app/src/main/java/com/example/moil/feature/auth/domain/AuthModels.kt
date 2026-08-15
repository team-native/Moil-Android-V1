package com.example.moil.feature.auth.domain

enum class VerificationStep { SignUp, Reset }

data class Verification(val verifyId: String)
data class VerifiedSession(val sessionId: String)
data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val userName: String? = null,
)
