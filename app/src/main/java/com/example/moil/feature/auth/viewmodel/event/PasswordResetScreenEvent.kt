package com.example.moil.feature.auth.viewmodel

sealed interface PasswordResetScreenEvent {
    data object BackClicked : PasswordResetScreenEvent
    data object LoginClicked : PasswordResetScreenEvent
    data class EmailChanged(val email: String) : PasswordResetScreenEvent
    data object SendCodeClicked : PasswordResetScreenEvent
    data class VerificationCodeChanged(val code: String) : PasswordResetScreenEvent
    data object ResendCodeClicked : PasswordResetScreenEvent
    data object VerifyCodeClicked : PasswordResetScreenEvent
    data class PasswordChanged(val password: String) : PasswordResetScreenEvent
    data class PasswordConfirmationChanged(val passwordConfirmation: String) : PasswordResetScreenEvent
    data object ResetPasswordClicked : PasswordResetScreenEvent
}
