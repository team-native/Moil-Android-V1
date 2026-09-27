package com.example.moil.feature.auth.module.domain.model

enum class VerificationStep { SignUp, Reset }

/** 현재 세션을 만든 로그인 방식이다. 비밀번호 변경·회원 탈퇴는 이메일 계정만 지원한다. */
enum class SignInMethod { Email, Social }

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
