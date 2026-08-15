package com.example.moil.feature.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun LoginRoute(
    initialEmail: String,
    onNavigateToSignUp: () -> Unit,
    onLoginCompleted: () -> Unit,
) {
    var loginUiState by remember(initialEmail) {
        mutableStateOf(LoginUiState(email = initialEmail))
    }

    LoginScreen(
        uiState = loginUiState,
        onEvent = { event ->
            when (event) {
                is LoginScreenEvent.EmailChanged -> {
                    loginUiState = loginUiState.copy(email = event.email)
                }

                is LoginScreenEvent.PasswordChanged -> {
                    loginUiState = loginUiState.copy(password = event.password)
                }

                LoginScreenEvent.SignUpClicked -> onNavigateToSignUp()
                LoginScreenEvent.LoginClicked -> onLoginCompleted()
            }
        },
    )
}
