package com.example.moil.feature.auth.module.data.oauth

import android.net.Uri
import com.example.moil.BuildConfig
import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.OAuthAuthorizationRequest
import com.example.moil.feature.auth.module.domain.model.SocialLoginCallback
import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.inject.Inject

class OAuthAuthorizationRequestFactory @Inject constructor(
    private val oauthAttemptStore: OAuthAttemptStore,
) {
    // Provider 로그인 화면을 열기 위한 URL을 만들고, callback 검증용 state/verifier를 메모리에 저장합니다.
    fun create(provider: SocialLoginProvider): MoilResult<OAuthAuthorizationRequest> {
        val state = randomUrlSafeString(STATE_LENGTH)
        val codeVerifier = randomUrlSafeString(CODE_VERIFIER_LENGTH)
        val codeChallenge = sha256Base64Url(codeVerifier)
        oauthAttemptStore.replace(
            OAuthAttempt(
                provider = provider,
                state = state,
                codeVerifier = codeVerifier,
            ),
        )

        // Provider client secret과 redirect 설정은 서버가 소유하므로 앱은 서버 시작 endpoint만 엽니다.
        val authorizationUrl = Uri.parse(BuildConfig.BASE_URL)
            .buildUpon()
            .appendPath("oauth")
            .appendPath(provider.wireValue)
            .appendQueryParameter("state", state)
            .appendQueryParameter("code_challenge", codeChallenge)
            .appendQueryParameter("code_challenge_method", "S256")
            .build()
            .toString()

        return MoilResult.Success(
            OAuthAuthorizationRequest(
                provider = provider,
                authorizationUrl = authorizationUrl,
            ),
        )
    }

    // callback state를 검증하고 성공한 시도에서만 백엔드 교환에 사용할 verifier를 꺼냅니다.
    fun consumeCodeVerifier(callback: SocialLoginCallback): String? =
        oauthAttemptStore.consume(callback.provider, callback.state)?.codeVerifier

    private fun randomUrlSafeString(length: Int): String {
        val randomBytes = ByteArray(length)
        secureRandom.nextBytes(randomBytes)
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(randomBytes)
            .take(length)
    }

    private fun sha256Base64Url(input: String): String = Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.US_ASCII)))

    private companion object {
        const val STATE_LENGTH = 32
        const val CODE_VERIFIER_LENGTH = 64
        val secureRandom = SecureRandom()
    }
}
