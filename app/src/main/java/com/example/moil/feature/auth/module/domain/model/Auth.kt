package com.example.moil.feature.auth.module.domain.model

enum class VerificationStep { SignUp, Reset }

enum class SocialLoginProvider(val wireValue: String) {
    Google("google"),
    Kakao("kakao"),
    Apple("apple"),
    ;

    companion object {
        fun fromWireValue(value: String): SocialLoginProvider? = entries.firstOrNull { provider ->
            provider.wireValue == value.lowercase()
        }
    }
}

data class OAuthAuthorizationRequest(
    val provider: SocialLoginProvider,
    val authorizationUrl: String,
)

data class SocialLoginCallback(
    val provider: SocialLoginProvider,
    val state: String,
    val accessToken: String,
    val refreshToken: String,
)

data class SocialLoginFailure(
    val provider: SocialLoginProvider,
    val state: String?,
)

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
