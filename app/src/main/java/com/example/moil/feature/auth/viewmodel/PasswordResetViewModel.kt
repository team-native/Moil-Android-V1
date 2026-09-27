package com.example.moil.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.VerificationStep
import com.example.moil.feature.auth.module.domain.usecase.ResetPasswordUseCase
import com.example.moil.feature.auth.module.domain.usecase.SendVerificationCodeUseCase
import com.example.moil.feature.auth.module.domain.usecase.VerifyCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.TimeSource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PasswordResetEffect {
    /** 비밀번호를 바꿨다. 로그인 화면으로 돌아가 이메일을 채워준다. */
    data class Completed(val email: String) : PasswordResetEffect

    /** 이메일 입력 단계에서 뒤로 가 로그인 화면으로 돌아간다. */
    data object ExitRequested : PasswordResetEffect
}

/**
 * 로그인 전 비밀번호 찾기 흐름을 담당한다.
 *
 * 인증 NavDisplay의 목적지 단위 ViewModelStore에 묶이므로 흐름을 벗어나면 입력한 비밀번호와 세션 ID가 함께 정리된다.
 */
@HiltViewModel
class PasswordResetViewModel @Inject constructor(
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(PasswordResetUiState())
    val uiState: StateFlow<PasswordResetUiState> = mutableUiState.asStateFlow()
    private val mutableEffects = MutableSharedFlow<PasswordResetEffect>()
    val effects: SharedFlow<PasswordResetEffect> = mutableEffects.asSharedFlow()

    /** 로그인 화면에 입력돼 있던 이메일로 첫 단계를 미리 채운다. 이미 입력을 시작했으면 덮어쓰지 않는다. */
    fun prefillEmail(email: String) {
        val currentState = mutableUiState.value

        if (email.isNotBlank() && currentState.email.isBlank()) {
            mutableUiState.value = currentState.copy(email = email)
        }
    }

    fun onEvent(event: PasswordResetScreenEvent) {
        when (event) {
            PasswordResetScreenEvent.BackClicked -> navigateBack()

            PasswordResetScreenEvent.LoginClicked -> {
                viewModelScope.launch { mutableEffects.emit(PasswordResetEffect.ExitRequested) }
            }

            is PasswordResetScreenEvent.EmailChanged -> {
                mutableUiState.value = mutableUiState.value.copy(
                    email = event.email,
                    error = null,
                )
            }

            PasswordResetScreenEvent.SendCodeClicked -> sendCode()

            is PasswordResetScreenEvent.VerificationCodeChanged -> {
                mutableUiState.value = mutableUiState.value.copy(
                    verificationCode = event.code,
                    isVerificationValidationShown = false,
                    error = null,
                )
            }

            PasswordResetScreenEvent.ResendCodeClicked -> {
                val codeIssuedAt = mutableUiState.value.codeIssuedAt

                // 서버는 발송 후 3분이 지나야 재발송을 허용하므로 그 전에는 요청하지 않는다.
                if (codeIssuedAt == null || codeIssuedAt.elapsedNow() >= VERIFICATION_CODE_RESEND_DELAY) {
                    sendCode()
                }
            }

            PasswordResetScreenEvent.VerifyCodeClicked -> verifyCode()

            is PasswordResetScreenEvent.PasswordChanged -> {
                mutableUiState.value = mutableUiState.value.copy(
                    password = event.password,
                    error = null,
                )
            }

            is PasswordResetScreenEvent.PasswordConfirmationChanged -> {
                mutableUiState.value = mutableUiState.value.copy(
                    passwordConfirmation = event.passwordConfirmation,
                    error = null,
                )
            }

            PasswordResetScreenEvent.ResetPasswordClicked -> resetPassword()
        }
    }

    /**
     * 인증번호 받기·재전송 버튼에서 호출되어 RESET 단계 인증번호를 보낸다.
     * 성공하면 인증번호 단계로 넘어가고 입력한 코드와 타이머 기준 시점을 새로 시작한다.
     */
    private fun sendCode() {
        val currentState = mutableUiState.value

        if (!currentState.canSendCode) {
            return
        }

        viewModelScope.launch {
            mutableUiState.value = currentState.copy(
                isLoading = true,
                error = null,
            )

            when (val result = sendVerificationCodeUseCase(null, currentState.email.trim(), VerificationStep.Reset)) {
                is MoilResult.Success -> {
                    mutableUiState.value = mutableUiState.value.copy(
                        isLoading = false,
                        currentStep = PasswordResetStep.Verification,
                        verifyId = result.value.verifyId,
                        verificationCode = "",
                        isVerificationValidationShown = false,
                        codeIssuedAt = TimeSource.Monotonic.markNow(),
                    )
                }

                is MoilResult.Failure -> {
                    mutableUiState.value = mutableUiState.value.copy(
                        isLoading = false,
                        error = result.error,
                    )
                }
            }
        }
    }

    /** 다음 버튼에서 호출되어 인증번호를 확인하고, 성공하면 발급된 초기화 세션으로 새 비밀번호 단계에 들어간다. */
    private fun verifyCode() {
        val currentState = mutableUiState.value
        val verifyId = currentState.verifyId

        if (verifyId == null || !currentState.canVerifyCode) {
            mutableUiState.value = currentState.copy(isVerificationValidationShown = true)
            return
        }

        viewModelScope.launch {
            mutableUiState.value = currentState.copy(
                isLoading = true,
                error = null,
            )

            when (val result = verifyCodeUseCase(verifyId, currentState.verificationCode)) {
                is MoilResult.Success -> {
                    mutableUiState.value = mutableUiState.value.copy(
                        isLoading = false,
                        currentStep = PasswordResetStep.NewPassword,
                        verifiedSessionId = result.value.sessionId,
                    )
                }

                is MoilResult.Failure -> {
                    mutableUiState.value = mutableUiState.value.copy(
                        isLoading = false,
                        isVerificationValidationShown = true,
                        error = result.error,
                    )
                }
            }
        }
    }

    /**
     * 비밀번호 변경 버튼에서 호출된다.
     * 성공하면 비밀번호와 세션 ID를 지운 뒤 로그인으로 돌아가는 완료 효과를 보낸다.
     */
    private fun resetPassword() {
        val currentState = mutableUiState.value
        val sessionId = currentState.verifiedSessionId

        if (sessionId == null || !currentState.canResetPassword) {
            return
        }

        viewModelScope.launch {
            mutableUiState.value = currentState.copy(
                isLoading = true,
                error = null,
            )

            when (val result = resetPasswordUseCase(sessionId, currentState.password, currentState.passwordConfirmation)) {
                is MoilResult.Success -> {
                    val email = currentState.email.trim()

                    mutableUiState.value = PasswordResetUiState(email = email)
                    mutableEffects.emit(PasswordResetEffect.Completed(email))
                }

                is MoilResult.Failure -> {
                    mutableUiState.value = mutableUiState.value.copy(
                        isLoading = false,
                        error = result.error,
                    )
                }
            }
        }
    }

    // 단계형 흐름의 뒤로가기다. 새 비밀번호 단계에서 돌아가면 발급된 세션은 버리고 인증번호를 다시 받게 한다.
    private fun navigateBack() {
        val currentState = mutableUiState.value

        when (currentState.currentStep) {
            PasswordResetStep.Email -> {
                viewModelScope.launch { mutableEffects.emit(PasswordResetEffect.ExitRequested) }
            }

            PasswordResetStep.Verification -> {
                mutableUiState.value = currentState.copy(
                    currentStep = PasswordResetStep.Email,
                    verificationCode = "",
                    isVerificationValidationShown = false,
                    error = null,
                )
            }

            PasswordResetStep.NewPassword -> {
                mutableUiState.value = currentState.copy(
                    currentStep = PasswordResetStep.Email,
                    verificationCode = "",
                    verifiedSessionId = null,
                    password = "",
                    passwordConfirmation = "",
                    codeIssuedAt = null,
                    error = null,
                )
            }
        }
    }
}
