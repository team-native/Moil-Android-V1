package com.example.moil.feature.auth.viewmodel

import com.example.moil.core.domain.MoilError
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeSource

/**
 * 비밀번호 찾기 3단계(이메일 → 인증번호 → 새 비밀번호)의 화면 상태다.
 *
 * [codeIssuedAt]은 인증번호를 마지막으로 받은 시점이다. 화면은 이 시점을 기준으로
 * 만료(5분)와 재발송 가능(3분) 카운트다운을 계산한다(API 명세 "이메일 인증").
 */
data class PasswordResetUiState(
    val currentStep: PasswordResetStep = PasswordResetStep.Email,
    val email: String = "",
    val verificationCode: String = "",
    val isVerificationValidationShown: Boolean = false,
    val password: String = "",
    val passwordConfirmation: String = "",
    val verifyId: String? = null,
    val verifiedSessionId: String? = null,
    val codeIssuedAt: TimeSource.Monotonic.ValueTimeMark? = null,
    val isLoading: Boolean = false,
    val error: MoilError? = null,
) {
    val canSendCode: Boolean
        get() = emailRegex.matches(email) && !isLoading

    val canVerifyCode: Boolean
        get() = verificationCode.length == PASSWORD_RESET_CODE_LENGTH &&
            verificationCode.all(Char::isDigit) &&
            !isLoading

    val canResetPassword: Boolean
        get() = passwordRegex.matches(password) &&
            password == passwordConfirmation &&
            verifiedSessionId != null &&
            !isLoading
}

enum class PasswordResetStep {
    Email,
    Verification,
    NewPassword,
}

internal const val PASSWORD_RESET_CODE_LENGTH = 6

/** 인증번호 만료 시간 (API 명세: 5분). */
val VERIFICATION_CODE_EXPIRATION: Duration = 5.minutes

/** 인증번호를 다시 받을 수 있기까지 기다리는 시간 (API 명세: 3분 후 재발송 가능). */
val VERIFICATION_CODE_RESEND_DELAY: Duration = 3.minutes
