package com.example.moil.feature.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginRoute(
    initialEmail: String,
    onNavigateToSignUp: () -> Unit,
    onLoginCompleted: () -> Unit,
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val loginUiState = viewModel.loginUiState.collectAsStateWithLifecycle()

    LaunchedEffect(initialEmail) {
        viewModel.setLoginEmail(initialEmail)
    }

    LoginScreen(
        uiState = loginUiState.value,
        onEvent = { event ->
            when (event) {
                is LoginScreenEvent.EmailChanged,
                is LoginScreenEvent.PasswordChanged,
                LoginScreenEvent.ForgotPasswordClicked -> Unit
                LoginScreenEvent.SignUpClicked -> onNavigateToSignUp()
                LoginScreenEvent.LoginClicked -> viewModel.onLoginEvent(event)
            }

            if (event is LoginScreenEvent.EmailChanged || event is LoginScreenEvent.PasswordChanged) {
                viewModel.onLoginEvent(event)
            }
        },
    )

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            if (effect is AuthEffect.LoginCompleted) onLoginCompleted()
        }
    }
}
