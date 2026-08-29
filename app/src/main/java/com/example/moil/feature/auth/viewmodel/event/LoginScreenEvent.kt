package com.example.moil.feature.auth.viewmodel

import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider

sealed interface LoginScreenEvent {
    data class EmailChanged(val email: String) : LoginScreenEvent
    data class PasswordChanged(val password: String) : LoginScreenEvent
    data object LoginClicked : LoginScreenEvent
    data object ForgotPasswordClicked : LoginScreenEvent
    data object SignUpClicked : LoginScreenEvent
    data class SocialLoginClicked(val provider: SocialLoginProvider) : LoginScreenEvent
}
