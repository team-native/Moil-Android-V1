package com.example.moil.core.network

import com.example.moil.feature.auth.module.data.dto.RefreshTokenRequestDto
import com.example.moil.feature.auth.module.data.remote.RefreshAuthApiService
import javax.inject.Inject
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenRefreshAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    private val refreshAuthApiService: dagger.Lazy<RefreshAuthApiService>,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 회원 탈퇴는 입력한 이메일·비밀번호가 틀려도 401을 준다. 이는 세션 만료가 아니므로
        // 토큰 갱신·강제 로그아웃 없이 호출부가 인증 실패로 처리하게 그대로 돌려준다.
        if (response.request.url.encodedPath.endsWith(DELETE_ACCOUNT_PATH)) {
            return null
        }

        if (responseCount(response) >= MAX_AUTH_ATTEMPTS) {
            return clearSession()
        }

        synchronized(this) {
            val previousAccessToken = response.request.header("Authorization")
                ?.removePrefix("Bearer ")
            val currentTokens = sessionManager.currentTokens() ?: return null

            if (currentTokens.accessToken != previousAccessToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer ${currentTokens.accessToken}")
                    .build()
            }

            val refreshResponse = try {
                refreshAuthApiService.get()
                    .refresh(RefreshTokenRequestDto(currentTokens.refreshToken))
                    .execute()
            } catch (_: Exception) {
                return clearSession()
            }
            val refreshEnvelope = refreshResponse.body()

            if (!refreshResponse.isSuccessful || refreshEnvelope?.success != true || refreshEnvelope.data == null) {
                return clearSession()
            }

            val refreshedTokens = SessionTokens(
                accessToken = refreshEnvelope.data.accessToken,
                refreshToken = refreshEnvelope.data.refreshToken,
            )
            if (refreshedTokens.accessToken.isBlank() || refreshedTokens.refreshToken.isBlank()) {
                return clearSession()
            }
            sessionManager.save(refreshedTokens)

            return response.request.newBuilder()
                .header("Authorization", "Bearer ${refreshedTokens.accessToken}")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var responseCursor: Response? = response
        var count = 0

        while (responseCursor != null) {
            count += 1
            responseCursor = responseCursor.priorResponse
        }

        return count
    }

    private fun clearSession(): Request? {
        sessionManager.expireSession()
        return null
    }

    private companion object {
        const val MAX_AUTH_ATTEMPTS = 2
        const val DELETE_ACCOUNT_PATH = "auth/delete-account"
    }
}
