package com.example.moil.navigation

import com.example.moil.core.domain.MoilError
import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.AuthSession
import com.example.moil.feature.auth.module.domain.model.OAuthAuthorizationRequest
import com.example.moil.feature.auth.module.domain.model.SocialLoginCallback
import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider
import com.example.moil.feature.auth.module.domain.model.SignInMethod
import com.example.moil.feature.auth.module.domain.model.UserProfile
import com.example.moil.feature.auth.module.domain.model.Verification
import com.example.moil.feature.auth.module.domain.model.VerificationStep
import com.example.moil.feature.auth.module.domain.model.VerifiedSession
import com.example.moil.feature.auth.module.domain.repository.AuthRepository
import com.example.moil.feature.auth.module.domain.repository.CurrentUserProfileStore
import com.example.moil.feature.auth.module.domain.usecase.ChangePasswordUseCase
import com.example.moil.feature.auth.module.domain.usecase.DeleteAccountUseCase
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
            changePasswordUseCase = ChangePasswordUseCase(profileRepository),
            deleteAccountUseCase = DeleteAccountUseCase(profileRepository),
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
            changePasswordUseCase = ChangePasswordUseCase(profileRepository),
            deleteAccountUseCase = DeleteAccountUseCase(profileRepository),
            currentUserProfileStore = FakeCurrentUserProfileStore(),
        )

        viewModel.updateProfileName("네이티브")
        advanceUntilIdle()

        assertEquals(
            ProfileUpdateUiState(saveError = ProfileEditSaveError.SaveFailed),
            viewModel.profileUpdateUiState.value,
        )
    }

    @Test
    fun `비밀번호 변경 성공은 완료 효과를 보내고 진행 상태를 해제한다`() = runTest {
        val repository = FakeAuthRepository(changePasswordResult = MoilResult.Success(Unit))
        val viewModel = createViewModel(repository)
        val effect = async(start = CoroutineStart.UNDISPATCHED) {
            viewModel.accountEffects.first()
        }

        viewModel.changePassword("old-password", "new-password", "new-password")

        assertEquals(AccountEffect.PasswordChanged, effect.await())
        assertEquals(AccountActionUiState(), viewModel.accountActionUiState.value)
    }

    @Test
    fun `비밀번호 변경 실패는 서버 사유를 오류 상태로 남긴다`() = runTest {
        val failure = MoilError.Server(status = 400, message = "기존 비밀번호가 일치하지 않습니다.")
        val viewModel = createViewModel(FakeAuthRepository(changePasswordResult = MoilResult.Failure(failure)))

        viewModel.changePassword("wrong-password", "new-password", "new-password")

        assertEquals(failure, viewModel.accountActionUiState.value.changePasswordError)
        assertEquals(false, viewModel.accountActionUiState.value.isChangingPassword)
    }

    @Test
    fun `회원 탈퇴는 일정 남기기 선택을 leftData로 전달하고 실패하면 오류를 남긴다`() = runTest {
        val failure = MoilError.Server(status = 401, message = "이메일 또는 비밀번호가 일치하지 않습니다.")
        val repository = FakeAuthRepository(deleteAccountResult = MoilResult.Failure(failure))
        val viewModel = createViewModel(repository)

        viewModel.deleteAccount("native@example.com", "password", shouldKeepSchedules = false)

        assertEquals(listOf(Triple("native@example.com", "password", false)), repository.deleteAccountRequests)
        assertEquals(failure, viewModel.accountActionUiState.value.deleteAccountError)
        assertEquals(false, viewModel.accountActionUiState.value.isDeletingAccount)
    }

    private fun createViewModel(repository: FakeAuthRepository): MainTabViewModel = MainTabViewModel(
        logoutUseCase = LogoutUseCase(repository),
        updateProfileNameUseCase = UpdateProfileNameUseCase(repository),
        changePasswordUseCase = ChangePasswordUseCase(repository),
        deleteAccountUseCase = DeleteAccountUseCase(repository),
        currentUserProfileStore = FakeCurrentUserProfileStore(),
    )
}

private class FakeCurrentUserProfileStore : CurrentUserProfileStore {
    private val mutableProfile = MutableStateFlow<UserProfile?>(null)

    override val profile: StateFlow<UserProfile?> = mutableProfile

    private val mutableSignInMethod = MutableStateFlow<SignInMethod?>(null)

    override val signInMethod: StateFlow<SignInMethod?> = mutableSignInMethod

    override fun save(profile: UserProfile) {
        mutableProfile.value = profile
    }

    override fun saveSignInMethod(signInMethod: SignInMethod) {
        mutableSignInMethod.value = signInMethod
    }

    override fun clear() {
        mutableProfile.value = null
    }
}

private class FakeAuthRepository(
    private val updateProfileResult: MoilResult<UserProfile> = MoilResult.Failure(MoilError.Network),
    private val changePasswordResult: MoilResult<Unit> = MoilResult.Failure(MoilError.Network),
    private val deleteAccountResult: MoilResult<Unit> = MoilResult.Failure(MoilError.Network),
) : AuthRepository {
    val deleteAccountRequests = mutableListOf<Triple<String, String, Boolean>>()

    override suspend fun updateProfileName(name: String): MoilResult<UserProfile> = updateProfileResult
    override suspend fun sendCode(name: String?, email: String, step: VerificationStep): MoilResult<Verification> = unused()
    override suspend fun verifyCode(verifyId: String, code: String): MoilResult<VerifiedSession> = unused()
    override suspend fun confirmSignUp(sessionId: String, password: String, passwordConfirmation: String, userName: String): MoilResult<AuthSession> = unused()
    override suspend fun login(email: String, password: String): MoilResult<AuthSession> = unused()
    override suspend fun startSocialLogin(provider: SocialLoginProvider): MoilResult<OAuthAuthorizationRequest> = unused()
    override suspend fun completeSocialLogin(callback: SocialLoginCallback): MoilResult<AuthSession> = unused()
    override fun cancelSocialLoginAttempt(provider: SocialLoginProvider, state: String?) = Unit
    override suspend fun resetPassword(sessionId: String, password: String, passwordConfirmation: String): MoilResult<Unit> = unused()
    override suspend fun changePassword(origin: String, newPassword: String, passwordConfirmation: String): MoilResult<Unit> = changePasswordResult
    override suspend fun logout(): MoilResult<Unit> = unused()
    override suspend fun deleteAccount(email: String, password: String, leaveData: Boolean): MoilResult<Unit> {
        deleteAccountRequests += Triple(email, password, leaveData)
        return deleteAccountResult
    }

    private fun <T> unused(): MoilResult<T> = MoilResult.Failure(MoilError.Network)
}
