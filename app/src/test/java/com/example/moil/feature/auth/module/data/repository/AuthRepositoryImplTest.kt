package com.example.moil.feature.auth.module.data.repository

import com.example.moil.core.domain.MoilResult
import com.example.moil.core.network.NetworkResult
import com.example.moil.core.network.SessionEvent
import com.example.moil.core.network.SessionManager
import com.example.moil.core.network.SessionState
import com.example.moil.core.network.SessionTokens
import com.example.moil.feature.auth.module.data.dto.ChangePasswordRequestDto
import com.example.moil.feature.auth.module.data.dto.DeleteAccountRequestDto
import com.example.moil.feature.auth.module.data.dto.LoginRequestDto
import com.example.moil.feature.auth.module.data.dto.PasswordSessionRequestDto
import com.example.moil.feature.auth.module.data.dto.SendCodeRequestDto
import com.example.moil.feature.auth.module.data.dto.TokenResponseDto
import com.example.moil.feature.auth.module.data.dto.UpdateProfileRequestDto
import com.example.moil.feature.auth.module.data.dto.UserProfileResponseDto
import com.example.moil.feature.auth.module.data.dto.VerificationResponseDto
import com.example.moil.feature.auth.module.data.dto.VerifiedSessionResponseDto
import com.example.moil.feature.auth.module.data.dto.VerifyCodeRequestDto
import com.example.moil.feature.auth.module.data.remote.AuthRemoteDataSource
import com.example.moil.feature.auth.module.domain.model.UserProfile
import com.example.moil.feature.auth.module.domain.repository.CurrentUserProfileStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryImplTest {
    @Test
    fun `프로필 변경 성공은 전체 서버 프로필을 로컬 프로필 저장소에 반영한다`() = runBlocking {
        val profileStore = FakeCurrentUserProfileStore()
        val repository = AuthRepositoryImpl(
            authRemoteDataSource = FakeAuthRemoteDataSource(
                updateProfileResult = NetworkResult.Success(
                    UserProfileResponseDto(1L, "네이티브", "native@example.com"),
                ),
            ),
            sessionManager = FakeSessionManager(),
            currentUserProfileStore = profileStore,
        )

        val result = repository.updateProfileName("네이티브")

        assertEquals(
            MoilResult.Success(UserProfile(1L, "네이티브", "native@example.com")),
            result,
        )
        assertEquals(UserProfile(1L, "네이티브", "native@example.com"), profileStore.profile.value)
    }

    @Test
    fun `로그아웃 성공은 프로필 저장소를 비우고 인증 세션을 종료한다`() = runBlocking {
        val profileStore = FakeCurrentUserProfileStore(UserProfile(1L, "네이티브", "native@example.com"))
        val sessionManager = FakeSessionManager()
        val repository = AuthRepositoryImpl(
            authRemoteDataSource = FakeAuthRemoteDataSource(logoutResult = NetworkResult.Success(Unit)),
            sessionManager = sessionManager,
            currentUserProfileStore = profileStore,
        )

        val result = repository.logout()

        assertEquals(MoilResult.Success(Unit), result)
        assertEquals(null, profileStore.profile.value)
        assertTrue(sessionManager.isExpired)
    }
}

private class FakeAuthRemoteDataSource(
    private val updateProfileResult: NetworkResult<UserProfileResponseDto> =
        NetworkResult.NetworkError(IllegalStateException("not used")),
    private val logoutResult: NetworkResult<Unit> = NetworkResult.NetworkError(IllegalStateException("not used")),
) : AuthRemoteDataSource {
    override suspend fun updateProfile(request: UpdateProfileRequestDto): NetworkResult<UserProfileResponseDto> = updateProfileResult
    override suspend fun sendCode(request: SendCodeRequestDto): NetworkResult<VerificationResponseDto> = unused()
    override suspend fun verifyCode(request: VerifyCodeRequestDto): NetworkResult<VerifiedSessionResponseDto> = unused()
    override suspend fun confirmSignUp(request: PasswordSessionRequestDto): NetworkResult<TokenResponseDto> = unused()
    override suspend fun login(request: LoginRequestDto): NetworkResult<TokenResponseDto> = unused()
    override suspend fun resetPassword(request: PasswordSessionRequestDto): NetworkResult<Unit> = unused()
    override suspend fun changePassword(request: ChangePasswordRequestDto): NetworkResult<Unit> = unused()
    override suspend fun logout(): NetworkResult<Unit> = logoutResult
    override suspend fun deleteAccount(request: DeleteAccountRequestDto): NetworkResult<Unit> = unused()

    private fun <T> unused(): NetworkResult<T> = NetworkResult.NetworkError(IllegalStateException("not used"))
}

private class FakeCurrentUserProfileStore(initialProfile: UserProfile? = null) : CurrentUserProfileStore {
    private val mutableProfile = MutableStateFlow(initialProfile)

    override val profile: StateFlow<UserProfile?> = mutableProfile

    override fun save(profile: UserProfile) {
        mutableProfile.value = profile
    }

    override fun clear() {
        mutableProfile.value = null
    }
}

private class FakeSessionManager : SessionManager {
    private val mutableSessionState = MutableStateFlow<SessionState>(SessionState.Authenticated)
    private val mutableSessionEvents = MutableSharedFlow<SessionEvent>()

    var isExpired = false
        private set

    override val sessionState: StateFlow<SessionState> = mutableSessionState
    override val sessionEvents: SharedFlow<SessionEvent> = mutableSessionEvents

    override fun currentTokens(): SessionTokens? = null
    override fun save(tokens: SessionTokens) = Unit

    override fun expireSession() {
        isExpired = true
        mutableSessionState.value = SessionState.Unauthenticated
    }
}
