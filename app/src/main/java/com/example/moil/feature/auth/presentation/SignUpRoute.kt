package com.example.moil.feature.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun SignUpRoute(
    onNavigateBack: () -> Unit,
    onSignUpCompleted: (String) -> Unit,
) {
    var signUpUiState by remember { mutableStateOf(SignUpUiState()) }

    SignUpScreen(
        uiState = signUpUiState,
        onEvent = { event ->
            when (event) {
                SignUpScreenEvent.BackClicked -> {
                    signUpUiState = when (signUpUiState.currentStep) {
                        SignUpStep.Information -> signUpUiState
                        SignUpStep.Verification -> signUpUiState.copy(currentStep = SignUpStep.Information)
                        SignUpStep.Password -> signUpUiState.copy(currentStep = SignUpStep.Verification)
                    }
                }

                is SignUpScreenEvent.NameChanged -> {
                    signUpUiState = signUpUiState.copy(name = event.name)
                }

                is SignUpScreenEvent.EmailChanged -> {
                    signUpUiState = signUpUiState.copy(email = event.email)
                }

                is SignUpScreenEvent.VerificationCodeChanged -> {
                    signUpUiState = signUpUiState.copy(
                        verificationCode = event.code,
                        isVerificationValidationShown = false,
                    )
                }

                is SignUpScreenEvent.PasswordChanged -> {
                    signUpUiState = signUpUiState.copy(password = event.password)
                }

                is SignUpScreenEvent.PasswordConfirmationChanged -> {
                    signUpUiState = signUpUiState.copy(passwordConfirmation = event.passwordConfirmation)
                }

                SignUpScreenEvent.NextClicked -> {
                    signUpUiState = when (signUpUiState.currentStep) {
                        SignUpStep.Information -> {
                            if (signUpUiState.name.isNotBlank() && signUpUiState.emailRegexMatches()) {
                                signUpUiState.copy(currentStep = SignUpStep.Verification)
                            } else {
                                signUpUiState
                            }
                        }

                        SignUpStep.Verification -> {
                            if (signUpUiState.isVerificationCodeValid()) {
                                signUpUiState.copy(currentStep = SignUpStep.Password)
                            } else {
                                signUpUiState.copy(isVerificationValidationShown = true)
                            }
                        }

                        SignUpStep.Password -> signUpUiState
                    }
                }

                SignUpScreenEvent.CreateAccountClicked -> {
                    if (signUpUiState.canCreateAccount()) {
                        onSignUpCompleted(signUpUiState.email)
                    }
                }

                SignUpScreenEvent.LoginClicked -> onNavigateBack()
            }
        },
    )
}

internal const val verificationCodeLength = 6

internal fun SignUpUiState.emailRegexMatches(): Boolean = emailRegex.matches(email)

internal fun SignUpUiState.canCreateAccount(): Boolean =
    passwordRegex.matches(password) && password == passwordConfirmation

internal fun SignUpUiState.isVerificationCodeValid(): Boolean =
    verificationCode.length == verificationCodeLength && verificationCode.all(Char::isDigit)
