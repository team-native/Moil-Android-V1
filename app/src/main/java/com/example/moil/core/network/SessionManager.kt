package com.example.moil.core.network

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class SessionTokens(
    val accessToken: String,
    val refreshToken: String,
)

sealed interface SessionState {
    data object Authenticated : SessionState
    data object Unauthenticated : SessionState
}

sealed interface SessionEvent {
    data object Expired : SessionEvent
}

interface SessionManager {
    val sessionState: StateFlow<SessionState>
    val sessionEvents: SharedFlow<SessionEvent>

    fun currentTokens(): SessionTokens?

    fun currentUserName(): String?

    fun save(tokens: SessionTokens, userName: String? = null)

    fun expireSession()
}

/** 앱 전체 인증 세션의 토큰과 로그인 상태를 단일하게 관리합니다. */
@Singleton
class DefaultSessionManager @Inject constructor(
    @ApplicationContext appContext: Context,
) : SessionManager {
    private val encryptedPreferences = EncryptedSharedPreferences.create(
        appContext,
        PREFERENCES_NAME,
        MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private val mutableTokens = MutableStateFlow(readTokens())
    private val mutableSessionState = MutableStateFlow(mutableTokens.value.toSessionState())
    private val mutableSessionEvents = MutableSharedFlow<SessionEvent>(extraBufferCapacity = 1)

    override val sessionState: StateFlow<SessionState> = mutableSessionState.asStateFlow()
    override val sessionEvents: SharedFlow<SessionEvent> = mutableSessionEvents.asSharedFlow()

    override fun currentTokens(): SessionTokens? = mutableTokens.value

    override fun currentUserName(): String? = encryptedPreferences
        .getString(USER_NAME_KEY, null)
        ?.takeIf(String::isNotBlank)

    override fun save(tokens: SessionTokens, userName: String?) {
        val sessionEditor = encryptedPreferences.edit()
            .putString(ACCESS_TOKEN_KEY, tokens.accessToken)
            .putString(REFRESH_TOKEN_KEY, tokens.refreshToken)

        userName
            ?.trim()
            ?.takeIf(String::isNotBlank)
            ?.let { normalizedUserName ->
                sessionEditor.putString(USER_NAME_KEY, normalizedUserName)
            }

        sessionEditor.apply()
        mutableTokens.value = tokens
        mutableSessionState.value = SessionState.Authenticated
    }

    override fun expireSession() {
        encryptedPreferences.edit().clear().apply()
        mutableTokens.value = null
        mutableSessionState.value = SessionState.Unauthenticated
        mutableSessionEvents.tryEmit(SessionEvent.Expired)
    }

    private fun readTokens(): SessionTokens? {
        val accessToken = encryptedPreferences.getString(ACCESS_TOKEN_KEY, null)
        val refreshToken = encryptedPreferences.getString(REFRESH_TOKEN_KEY, null)

        return if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
            null
        } else {
            SessionTokens(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        }
    }

    private fun SessionTokens?.toSessionState(): SessionState = if (this == null) {
        SessionState.Unauthenticated
    } else {
        SessionState.Authenticated
    }

    private companion object {
        const val PREFERENCES_NAME = "moil_auth_tokens"
        const val ACCESS_TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
        const val USER_NAME_KEY = "user_name"
    }
}
