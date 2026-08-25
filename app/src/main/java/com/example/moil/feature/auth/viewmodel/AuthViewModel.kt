package com.example.moil.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.VerificationStep
import com.example.moil.feature.auth.module.domain.usecase.ConfirmSignUpUseCase
import com.example.moil.feature.auth.module.domain.usecase.LoginUseCase
import com.example.moil.feature.auth.module.domain.usecase.SendVerificationCodeUseCase
import com.example.moil.feature.auth.module.domain.usecase.VerifyCodeUseCase
import com.example.moil.feature.auth.view.canCreateAccount
import com.example.moil.feature.auth.view.canLogin
import com.example.moil.feature.auth.view.emailRegexMatches
import com.example.moil.feature.auth.view.isVerificationCodeValid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthEffect {
    data object LoginCompleted : AuthEffect
    data class SignUpCompleted(val email: String) : AuthEffect
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val confirmSignUpUseCase: ConfirmSignUpUseCase,
    private val loginUseCase: LoginUseCase,
) : ViewModel() {
    private val mutableLoginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = mutableLoginUiState.asStateFlow()
    private val mutableSignUpUiState = MutableStateFlow(SignUpUiState())
    val signUpUiState: StateFlow<SignUpUiState> = mutableSignUpUiState.asStateFlow()
    private val mutableEffects = MutableSharedFlow<AuthEffect>()
    val effects: SharedFlow<AuthEffect> = mutableEffects.asSharedFlow()

    fun setLoginEmail(email: String) {
        if (email.isNotBlank() && mutableLoginUiState.value.email != email) {
            mutableLoginUiState.value = mutableLoginUiState.value.copy(email = email)
        }
    }

    fun onLoginEvent(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.EmailChanged -> mutableLoginUiState.value = mutableLoginUiState.value.copy(email = event.email, errorMessage = null)
            is LoginScreenEvent.PasswordChanged -> mutableLoginUiState.value = mutableLoginUiState.value.copy(password = event.password, errorMessage = null)
            LoginScreenEvent.LoginClicked -> login()
            LoginScreenEvent.ForgotPasswordClicked, LoginScreenEvent.SignUpClicked -> Unit
        }
    }

    fun onSignUpEvent(event: SignUpScreenEvent) {
        when (event) {
            SignUpScreenEvent.BackClicked -> navigateBack()
            is SignUpScreenEvent.NameChanged -> mutableSignUpUiState.value = mutableSignUpUiState.value.copy(name = event.name, errorMessage = null)
            is SignUpScreenEvent.EmailChanged -> mutableSignUpUiState.value = mutableSignUpUiState.value.copy(email = event.email, errorMessage = null)
            is SignUpScreenEvent.VerificationCodeChanged -> mutableSignUpUiState.value = mutableSignUpUiState.value.copy(verificationCode = event.code, isVerificationValidationShown = false)
            is SignUpScreenEvent.PasswordChanged -> mutableSignUpUiState.value = mutableSignUpUiState.value.copy(password = event.password, errorMessage = null)
            is SignUpScreenEvent.PasswordConfirmationChanged -> mutableSignUpUiState.value = mutableSignUpUiState.value.copy(passwordConfirmation = event.passwordConfirmation, errorMessage = null)
            SignUpScreenEvent.NextClicked -> advanceSignUp()
            SignUpScreenEvent.CreateAccountClicked -> confirmSignUp()
            SignUpScreenEvent.LoginClicked -> Unit
        }
    }

    /** 로그인 버튼 이벤트에서 유효한 입력만 로그인 UseCase로 전달하고 성공 시 메인 이동 효과를 보냅니다. */
    private fun login() {
        val state = mutableLoginUiState.value
        if (!state.canLogin()) return

        viewModelScope.launch {
            mutableLoginUiState.value = state.copy(isLoading = true, errorMessage = null)
            when (val result = loginUseCase(state.email, state.password)) {
                is MoilResult.Success -> {
                    mutableLoginUiState.value = mutableLoginUiState.value.copy(isLoading = false)
                    mutableEffects.emit(AuthEffect.LoginCompleted)
                }

                is MoilResult.Failure -> {
                    mutableLoginUiState.value = mutableLoginUiState.value.copy(
                        isLoading = false,
                        errorMessage = result.error.toMessage(),
                    )
                }
            }
        }
    }

    /** 회원가입 다음 버튼에서 호출되어 현재 단계별 인증 UseCase를 실행합니다. */
    private fun advanceSignUp() = viewModelScope.launch {
        when (mutableSignUpUiState.value.currentStep) {
            SignUpStep.Information -> sendCode()
            SignUpStep.Verification -> verifyCode()
            SignUpStep.Password -> Unit
        }
    }

    private suspend fun sendCode() {
        val state = mutableSignUpUiState.value
        if (state.name.isBlank() || !state.emailRegexMatches()) return
        mutableSignUpUiState.value = state.copy(isLoading = true, errorMessage = null)
        when (val result = sendVerificationCodeUseCase(state.name, state.email, VerificationStep.SignUp)) {
            is MoilResult.Success -> mutableSignUpUiState.value = mutableSignUpUiState.value.copy(isLoading = false, currentStep = SignUpStep.Verification, verifyId = result.value.verifyId)
            is MoilResult.Failure -> mutableSignUpUiState.value = mutableSignUpUiState.value.copy(isLoading = false, errorMessage = result.error.toMessage())
        }
    }

    private suspend fun verifyCode() {
        val state = mutableSignUpUiState.value
        val verifyId = state.verifyId
        if (verifyId == null || !state.isVerificationCodeValid()) {
            mutableSignUpUiState.value = state.copy(isVerificationValidationShown = true)
            return
        }
        mutableSignUpUiState.value = state.copy(isLoading = true, errorMessage = null)
        when (val result = verifyCodeUseCase(verifyId, state.verificationCode)) {
            is MoilResult.Success -> mutableSignUpUiState.value = mutableSignUpUiState.value.copy(isLoading = false, currentStep = SignUpStep.Password, verifiedSessionId = result.value.sessionId)
            is MoilResult.Failure -> mutableSignUpUiState.value = mutableSignUpUiState.value.copy(isLoading = false, isVerificationValidationShown = true, errorMessage = result.error.toMessage())
        }
    }

    /** 계정 만들기 이벤트에서 회원가입 UseCase를 실행하고 토큰 저장 뒤 로그인 이동 효과를 보냅니다. */
    private fun confirmSignUp() = viewModelScope.launch {
        val state = mutableSignUpUiState.value
        val sessionId = state.verifiedSessionId
        if (sessionId == null || !state.canCreateAccount()) return@launch
        mutableSignUpUiState.value = state.copy(isLoading = true, errorMessage = null)
        when (
            val result = confirmSignUpUseCase(
                sessionId = sessionId,
                password = state.password,
                confirmation = state.passwordConfirmation,
                userName = state.name,
            )
        ) {
            is MoilResult.Success -> {
                mutableSignUpUiState.value = mutableSignUpUiState.value.copy(isLoading = false)
                mutableEffects.emit(AuthEffect.SignUpCompleted(state.email))
            }
            is MoilResult.Failure -> mutableSignUpUiState.value = mutableSignUpUiState.value.copy(isLoading = false, errorMessage = result.error.toMessage())
        }
    }

    private fun navigateBack() {
        val state = mutableSignUpUiState.value
        mutableSignUpUiState.value = when (state.currentStep) {
            SignUpStep.Information -> state
            SignUpStep.Verification -> state.copy(currentStep = SignUpStep.Information)
            SignUpStep.Password -> state.copy(currentStep = SignUpStep.Verification)
        }
    }
}

private fun com.example.moil.core.domain.MoilError.toMessage(): String = when (this) {
    is com.example.moil.core.domain.MoilError.Server -> message
    is com.example.moil.core.domain.MoilError.Http -> message
    com.example.moil.core.domain.MoilError.Network -> "네트워크 연결을 확인해주세요."
}
