package com.example.moil.feature.auth.viewmodel

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
import com.example.moil.feature.auth.module.domain.usecase.ResetPasswordUseCase
import com.example.moil.feature.auth.module.domain.usecase.SendVerificationCodeUseCase
import com.example.moil.feature.auth.module.domain.usecase.VerifyCodeUseCase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PasswordResetViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `인증번호는 RESET 단계로 요청하고 성공하면 인증 단계로 넘어간다`() = runTest {
        val repository = FakePasswordResetRepository()
        val viewModel = createViewModel(repository)

        viewModel.onEvent(PasswordResetScreenEvent.EmailChanged("native@example.com"))
        viewModel.onEvent(PasswordResetScreenEvent.SendCodeClicked)

        assertEquals(listOf("native@example.com" to VerificationStep.Reset), repository.sendCodeRequests)
        assertEquals(PasswordResetStep.Verification, viewModel.uiState.value.currentStep)
        assertNotNull(viewModel.uiState.value.codeIssuedAt)
    }

    @Test
    fun `발송 후 3분이 지나기 전의 재전송 요청은 서버로 보내지 않는다`() = runTest {
        val repository = FakePasswordResetRepository()
        val viewModel = createViewModel(repository)

        viewModel.onEvent(PasswordResetScreenEvent.EmailChanged("native@example.com"))
        viewModel.onEvent(PasswordResetScreenEvent.SendCodeClicked)
        viewModel.onEvent(PasswordResetScreenEvent.ResendCodeClicked)

        assertEquals(1, repository.sendCodeRequests.size)
    }

    @Test
    fun `인증 후 새 비밀번호를 저장하면 입력값을 지우고 완료 효과를 보낸다`() = runTest {
        val repository = FakePasswordResetRepository()
        val viewModel = createViewModel(repository)
        val effect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.effects.first() }

        viewModel.onEvent(PasswordResetScreenEvent.EmailChanged("native@example.com"))
        viewModel.onEvent(PasswordResetScreenEvent.SendCodeClicked)
        viewModel.onEvent(PasswordResetScreenEvent.VerificationCodeChanged("123456"))
        viewModel.onEvent(PasswordResetScreenEvent.VerifyCodeClicked)
        viewModel.onEvent(PasswordResetScreenEvent.PasswordChanged("new-password"))
        viewModel.onEvent(PasswordResetScreenEvent.PasswordConfirmationChanged("new-password"))
        viewModel.onEvent(PasswordResetScreenEvent.ResetPasswordClicked)

        assertEquals(PasswordResetEffect.Completed("native@example.com"), effect.await())
        assertEquals(listOf("reset-session"), repository.resetSessionIds)
        assertEquals("", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.verifiedSessionId)
    }

    @Test
    fun `인증번호 확인 실패는 서버 오류를 보여주고 같은 단계에 머문다`() = runTest {
        val failure = MoilError.Server(status = 400, message = "인증번호가 일치하지 않습니다.")
        val repository = FakePasswordResetRepository(verifyResult = MoilResult.Failure(failure))
        val viewModel = createViewModel(repository)

        viewModel.onEvent(PasswordResetScreenEvent.EmailChanged("native@example.com"))
        viewModel.onEvent(PasswordResetScreenEvent.SendCodeClicked)
        viewModel.onEvent(PasswordResetScreenEvent.VerificationCodeChanged("000000"))
        viewModel.onEvent(PasswordResetScreenEvent.VerifyCodeClicked)

        assertEquals(PasswordResetStep.Verification, viewModel.uiState.value.currentStep)
        assertEquals(failure, viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.isVerificationValidationShown)
    }

    private fun createViewModel(repository: FakePasswordResetRepository) = PasswordResetViewModel(
        sendVerificationCodeUseCase = SendVerificationCodeUseCase(repository),
        verifyCodeUseCase = VerifyCodeUseCase(repository),
        resetPasswordUseCase = ResetPasswordUseCase(repository),
    )
}

private class FakePasswordResetRepository(
    private val verifyResult: MoilResult<VerifiedSession> = MoilResult.Success(VerifiedSession("reset-session")),
) : AuthRepository {
    val sendCodeRequests = mutableListOf<Pair<String, VerificationStep>>()
    val resetSessionIds = mutableListOf<String>()

    override suspend fun sendCode(name: String?, email: String, step: VerificationStep): MoilResult<Verification> {
        sendCodeRequests += email to step
        return MoilResult.Success(Verification("verify-id"))
    }

    override suspend fun verifyCode(verifyId: String, code: String): MoilResult<VerifiedSession> = verifyResult

    override suspend fun resetPassword(sessionId: String, password: String, passwordConfirmation: String): MoilResult<Unit> {
        resetSessionIds += sessionId
        return MoilResult.Success(Unit)
    }

    override suspend fun updateProfileName(name: String): MoilResult<UserProfile> = unused()
    override suspend fun confirmSignUp(sessionId: String, password: String, passwordConfirmation: String, userName: String): MoilResult<AuthSession> = unused()
    override suspend fun login(email: String, password: String): MoilResult<AuthSession> = unused()
    override suspend fun startSocialLogin(provider: SocialLoginProvider): MoilResult<OAuthAuthorizationRequest> = unused()
    override suspend fun completeSocialLogin(callback: SocialLoginCallback): MoilResult<AuthSession> = unused()
    override fun cancelSocialLoginAttempt(provider: SocialLoginProvider, state: String?) = Unit
    override suspend fun changePassword(origin: String, newPassword: String, passwordConfirmation: String): MoilResult<Unit> = unused()
    override suspend fun logout(): MoilResult<Unit> = unused()
    override suspend fun deleteAccount(email: String, password: String, leaveData: Boolean): MoilResult<Unit> = unused()

    private fun <T> unused(): MoilResult<T> = MoilResult.Failure(MoilError.Network)
}
