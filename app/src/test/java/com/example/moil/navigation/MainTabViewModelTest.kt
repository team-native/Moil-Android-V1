package com.example.moil.navigation

import com.example.moil.core.domain.MoilError
import com.example.moil.core.domain.MoilResult
import com.example.moil.core.network.SessionEvent
import com.example.moil.core.network.SessionManager
import com.example.moil.core.network.SessionState
import com.example.moil.core.network.SessionTokens
import com.example.moil.feature.auth.domain.AuthRepository
import com.example.moil.feature.auth.domain.AuthSession
import com.example.moil.feature.auth.domain.ChangePasswordUseCase
import com.example.moil.feature.auth.domain.LogoutUseCase
import com.example.moil.feature.auth.domain.UpdateProfileNameUseCase
import com.example.moil.feature.auth.domain.UserProfile
import com.example.moil.feature.auth.domain.Verification
import com.example.moil.feature.auth.domain.VerificationStep
import com.example.moil.feature.auth.domain.VerifiedSession
import com.example.moil.feature.profile.presentation.ProfileEditSaveError
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainTabViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test
    fun `프로필 저장 성공은 세션 이름을 갱신하고 저장 효과를 발행한다`() = runTest {
        val sessionManager = FakeSessionManager()
        val profileRepository = FakeAuthRepository(
            updateProfileResult = MoilResult.Success(UserProfile(1L, "네이티브", "native@example.com")),
        )
        val viewModel = MainTabViewModel(
            logoutUseCase = LogoutUseCase(profileRepository),
            updateProfileNameUseCase = UpdateProfileNameUseCase(profileRepository),
            sessionManager = sessionManager,
        )
        val savedEffect = async(start = CoroutineStart.UNDISPATCHED) {
            viewModel.profileUpdateEffects.first()
        }

        viewModel.updateProfileName("네이티브")
        advanceUntilIdle()

        assertEquals("네이티브", sessionManager.currentUserName())
        assertEquals(ProfileUpdateUiState(), viewModel.profileUpdateUiState.value)
        assertEquals(ProfileUpdateEffect.Saved("네이티브"), savedEffect.await())
    }

    @Test
    fun `프로필 저장 실패는 입력을 유지할 수 있는 오류 상태를 남긴다`() = runTest {
        val profileRepository = FakeAuthRepository(
            updateProfileResult = MoilResult.Failure(MoilError.Network),
        )
        val viewModel = MainTabViewModel(
            logoutUseCase = LogoutUseCase(profileRepository),
            updateProfileNameUseCase = UpdateProfileNameUseCase(profileRepository),
            sessionManager = FakeSessionManager(),
        )

        viewModel.updateProfileName("네이티브")
        advanceUntilIdle()

        assertEquals(
            ProfileUpdateUiState(saveError = ProfileEditSaveError.SaveFailed),
            viewModel.profileUpdateUiState.value,
        )
    }
}

private class FakeAuthRepository(
    private val updateProfileResult: MoilResult<UserProfile>,
) : AuthRepository {
    override suspend fun updateProfileName(name: String): MoilResult<UserProfile> = updateProfileResult
    override suspend fun sendCode(name: String?, email: String, step: VerificationStep): MoilResult<Verification> = unused()
    override suspend fun verifyCode(verifyId: String, code: String): MoilResult<VerifiedSession> = unused()
    override suspend fun confirmSignUp(sessionId: String, password: String, passwordConfirmation: String, userName: String): MoilResult<AuthSession> = unused()
    override suspend fun login(email: String, password: String): MoilResult<AuthSession> = unused()
    override suspend fun resetPassword(sessionId: String, password: String, passwordConfirmation: String): MoilResult<Unit> = unused()
    override suspend fun changePassword(origin: String, newPassword: String, passwordConfirmation: String): MoilResult<Unit> = unused()
    override suspend fun logout(): MoilResult<Unit> = unused()
    override suspend fun deleteAccount(email: String, password: String, leaveData: Boolean): MoilResult<Unit> = unused()

    private fun <T> unused(): MoilResult<T> = MoilResult.Failure(MoilError.Network)
}

private class FakeSessionManager : SessionManager {
    private var userName: String? = null
    private val mutableSessionState = MutableStateFlow<SessionState>(SessionState.Unauthenticated)
    private val mutableSessionEvents = MutableSharedFlow<SessionEvent>()

    override val sessionState: StateFlow<SessionState> = mutableSessionState
    override val sessionEvents: SharedFlow<SessionEvent> = mutableSessionEvents

    override fun currentTokens(): SessionTokens? = null
    override fun currentUserName(): String? = userName
    override fun save(tokens: SessionTokens, userName: String?) = Unit
    override fun updateUserName(userName: String) {
        this.userName = userName
    }
    override fun expireSession() = Unit
}
