package com.example.moil.feature.auth.module.data.oauth

import android.net.Uri
import com.example.moil.BuildConfig
import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.OAuthAuthorizationRequest
import com.example.moil.feature.auth.module.domain.model.SocialLoginCallback
import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider
import java.security.SecureRandom
import java.util.Base64
import javax.inject.Inject

class OAuthAuthorizationRequestFactory @Inject constructor(
    private val oauthAttemptStore: OAuthAttemptStore,
) {
    // 서버 OAuth 시작 URL과 callback 위조 방지용 일회성 state를 만듭니다.
    fun create(provider: SocialLoginProvider): MoilResult<OAuthAuthorizationRequest> {
        val state = randomUrlSafeString(STATE_LENGTH)
        oauthAttemptStore.replace(
            OAuthAttempt(
                provider = provider,
                state = state,
            ),
        )

        // Provider client secret과 redirect 설정은 서버가 소유하므로 앱은 서버 시작 endpoint만 엽니다.
        val authorizationUrl = Uri.parse(BuildConfig.BASE_URL)
            .buildUpon()
            .appendPath("oauth")
            .appendPath(provider.wireValue)
            .appendQueryParameter("state", state)
            .build()
            .toString()

        return MoilResult.Success(
            OAuthAuthorizationRequest(
                provider = provider,
                authorizationUrl = authorizationUrl,
            ),
        )
    }

    // 서버 callback의 provider/state가 현재 인증 시도와 일치할 때만 토큰을 수용합니다.
    fun consumeAttempt(provider: SocialLoginProvider, state: String): Boolean =
        oauthAttemptStore.consume(provider, state)

    // 취소·실패 callback도 일치하는 시도만 폐기해 외부 링크가 진행 중인 인증을 취소하지 못하게 합니다.
    fun discardAttempt(provider: SocialLoginProvider, state: String?): Boolean =
        state != null && oauthAttemptStore.consume(provider, state)

    private fun randomUrlSafeString(length: Int): String {
        val randomBytes = ByteArray(length)
        secureRandom.nextBytes(randomBytes)
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(randomBytes)
            .take(length)
    }

    private companion object {
        const val STATE_LENGTH = 32
        val secureRandom = SecureRandom()
    }
}
