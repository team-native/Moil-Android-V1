package com.example.moil.feature.auth.module.data.oauth

import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider
import javax.inject.Inject
import javax.inject.Singleton

data class OAuthAttempt(
    val provider: SocialLoginProvider,
    val state: String,
)

/** 인증 시도에 필요한 일회성 문자열만 보관하며 Activity나 callback을 저장하지 않습니다. */
@Singleton
class OAuthAttemptStore @Inject constructor() {
    private var currentAttempt: OAuthAttempt? = null

    @Synchronized
    fun replace(attempt: OAuthAttempt) {
        currentAttempt = attempt
    }

    @Synchronized
    fun consume(provider: SocialLoginProvider, state: String): Boolean {
        val attempt = currentAttempt
        val isMatchingAttempt = attempt?.let { storedAttempt ->
            storedAttempt.provider == provider && storedAttempt.state == state
        } ?: false
        if (isMatchingAttempt) {
            currentAttempt = null
        }
        return isMatchingAttempt
    }

    @Synchronized
    fun clear() {
        currentAttempt = null
    }
}
