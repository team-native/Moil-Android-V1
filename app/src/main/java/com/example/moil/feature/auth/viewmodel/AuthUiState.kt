package com.example.moil.feature.auth.viewmodel

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

data class SignUpUiState(
    val currentStep: SignUpStep = SignUpStep.Information,
    val name: String = "",
    val email: String = "",
    val verificationCode: String = "",
    val isVerificationValidationShown: Boolean = false,
    val password: String = "",
    val passwordConfirmation: String = "",
    val verifyId: String? = null,
    val verifiedSessionId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

enum class SignUpStep {
    Information,
    Verification,
    Password,
}

internal val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
internal val passwordRegex = Regex("^.{8,}$")
