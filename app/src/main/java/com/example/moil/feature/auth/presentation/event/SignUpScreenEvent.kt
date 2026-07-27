package com.example.moil.feature.auth.presentation

sealed interface SignUpScreenEvent {
    data object BackClicked : SignUpScreenEvent
    data class NameChanged(val name: String) : SignUpScreenEvent
    data class EmailChanged(val email: String) : SignUpScreenEvent
    data class VerificationCodeChanged(val code: String) : SignUpScreenEvent
    data class PasswordChanged(val password: String) : SignUpScreenEvent
    data class PasswordConfirmationChanged(val passwordConfirmation: String) : SignUpScreenEvent
    data object NextClicked : SignUpScreenEvent
    data object CreateAccountClicked : SignUpScreenEvent
    data object LoginClicked : SignUpScreenEvent
}
