package com.example.moil.feature.auth.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moil.feature.auth.viewmodel.AuthEffect
import com.example.moil.feature.auth.viewmodel.AuthViewModel
import com.example.moil.feature.auth.viewmodel.LoginUiState
import com.example.moil.feature.auth.viewmodel.SignUpScreenEvent
import com.example.moil.feature.auth.viewmodel.SignUpUiState
import com.example.moil.feature.auth.viewmodel.emailRegex
import com.example.moil.feature.auth.viewmodel.passwordRegex

@Composable
fun SignUpRoute(
    onNavigateBack: () -> Unit,
    onSignUpCompleted: (String) -> Unit,
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val signUpUiState = viewModel.signUpUiState.collectAsStateWithLifecycle()

    SignUpScreen(
        uiState = signUpUiState.value,
        onEvent = { event ->
            if (event == SignUpScreenEvent.LoginClicked) {
                onNavigateBack()
            } else {
                viewModel.onSignUpEvent(event)
            }
        },
    )

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            if (effect is AuthEffect.SignUpCompleted) {
                onSignUpCompleted(effect.email)
            }
        }
    }
}

internal const val verificationCodeLength = 6

internal fun SignUpUiState.emailRegexMatches(): Boolean = emailRegex.matches(email)

internal fun LoginUiState.canLogin(): Boolean =
    emailRegex.matches(email) && passwordRegex.matches(password)

internal fun SignUpUiState.canCreateAccount(): Boolean =
    passwordRegex.matches(password) && password == passwordConfirmation

internal fun SignUpUiState.isVerificationCodeValid(): Boolean =
    verificationCode.length == verificationCodeLength && verificationCode.all(Char::isDigit)
