package com.example.moil.navigation

import com.example.moil.core.domain.MoilError
import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.AuthSession
import com.example.moil.feature.auth.module.domain.model.OAuthAuthorizationRequest
import com.example.moil.feature.auth.module.domain.model.SocialLoginCallback
import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider
import com.example.moil.feature.auth.module.domain.model.UserProfile
import com.example.moil.feature.auth.module.domain.model.Verification
import com.example.moil.feature.auth.module.domain.model.VerificationStep
import com.example.moil.feature.auth.module.domain.model.VerifiedSession
import com.example.moil.feature.auth.module.domain.repository.AuthRepository
import com.example.moil.feature.auth.module.domain.repository.CurrentUserProfileStore
import com.example.moil.feature.auth.module.domain.usecase.LogoutUseCase
import com.example.moil.feature.auth.module.domain.usecase.UpdateProfileNameUseCase
import com.example.moil.feature.profile.viewmodel.ProfileEditSaveError
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    fun `프로필 저장 성공은 전체 프로필을 담은 저장 효과를 발행한다`() = runTest {
        val profileRepository = FakeAuthRepository(
            updateProfileResult = MoilResult.Success(UserProfile(1L, "네이티브", "native@example.com")),
        )
        val viewModel = MainTabViewModel(
            logoutUseCase = LogoutUseCase(profileRepository),
            updateProfileNameUseCase = UpdateProfileNameUseCase(profileRepository),
            currentUserProfileStore = FakeCurrentUserProfileStore(),
        )
        val savedEffect = async(start = CoroutineStart.UNDISPATCHED) {
            viewModel.profileUpdateEffects.first()
        }

        viewModel.updateProfileName("네이티브")
        advanceUntilIdle()

        assertEquals(ProfileUpdateUiState(), viewModel.profileUpdateUiState.value)
        assertEquals(
            ProfileUpdateEffect.Saved(UserProfile(1L, "네이티브", "native@example.com")),
            savedEffect.await(),
        )
    }

    @Test
    fun `프로필 저장 실패는 입력을 유지할 수 있는 오류 상태를 남긴다`() = runTest {
        val profileRepository = FakeAuthRepository(
            updateProfileResult = MoilResult.Failure(MoilError.Network),
        )
        val viewModel = MainTabViewModel(
            logoutUseCase = LogoutUseCase(profileRepository),
            updateProfileNameUseCase = UpdateProfileNameUseCase(profileRepository),
            currentUserProfileStore = FakeCurrentUserProfileStore(),
        )

        viewModel.updateProfileName("네이티브")
        advanceUntilIdle()

        assertEquals(
            ProfileUpdateUiState(saveError = ProfileEditSaveError.SaveFailed),
            viewModel.profileUpdateUiState.value,
        )
    }
}

private class FakeCurrentUserProfileStore : CurrentUserProfileStore {
    private val mutableProfile = MutableStateFlow<UserProfile?>(null)

    override val profile: StateFlow<UserProfile?> = mutableProfile

    override fun save(profile: UserProfile) {
        mutableProfile.value = profile
    }

    override fun clear() {
        mutableProfile.value = null
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
    override suspend fun startSocialLogin(provider: SocialLoginProvider): MoilResult<OAuthAuthorizationRequest> = unused()
    override suspend fun completeSocialLogin(callback: SocialLoginCallback): MoilResult<AuthSession> = unused()
    override suspend fun resetPassword(sessionId: String, password: String, passwordConfirmation: String): MoilResult<Unit> = unused()
    override suspend fun changePassword(origin: String, newPassword: String, passwordConfirmation: String): MoilResult<Unit> = unused()
    override suspend fun logout(): MoilResult<Unit> = unused()
    override suspend fun deleteAccount(email: String, password: String, leaveData: Boolean): MoilResult<Unit> = unused()

    private fun <T> unused(): MoilResult<T> = MoilResult.Failure(MoilError.Network)
}
