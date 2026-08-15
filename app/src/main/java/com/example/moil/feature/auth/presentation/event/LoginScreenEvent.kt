package com.example.moil.feature.auth.presentation

sealed interface LoginScreenEvent {
    data class EmailChanged(val email: String) : LoginScreenEvent
    data class PasswordChanged(val password: String) : LoginScreenEvent
    data object LoginClicked : LoginScreenEvent
    data object ForgotPasswordClicked : LoginScreenEvent
    data object SignUpClicked : LoginScreenEvent
}
