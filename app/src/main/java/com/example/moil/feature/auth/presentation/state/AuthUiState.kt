package com.example.moil.feature.auth.presentation

data class LoginUiState(
    val email: String = "",
    val password: String = "",
)

data class SignUpUiState(
    val currentStep: SignUpStep = SignUpStep.Information,
    val name: String = "",
    val email: String = "",
    val verificationCode: String = "",
    val isVerificationValidationShown: Boolean = false,
    val password: String = "",
    val passwordConfirmation: String = "",
)

enum class SignUpStep {
    Information,
    Verification,
    Password,
}

internal val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
internal val passwordRegex = Regex("^.{8,}$")
