package com.example.moil.feature.auth.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.core.component.MoilTextField
import com.example.moil.ui.theme.MoilAuthDimension

@Composable
internal fun LoginScreenContent(
    uiState: LoginUiState,
    onEvent: (LoginScreenEvent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = MoilAuthDimension.ScreenHorizontalPadding)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            AuthBranding()

            Spacer(modifier = Modifier.height(36.dp))

            MoilTextField(
                value = uiState.email,
                onValueChange = { email ->
                    onEvent(LoginScreenEvent.EmailChanged(email))
                },
                placeholder = stringResource(R.string.auth_email),
                keyboardType = KeyboardType.Email,
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.FieldSpacing))

            MoilTextField(
                value = uiState.password,
                onValueChange = { password ->
                    onEvent(LoginScreenEvent.PasswordChanged(password))
                },
                placeholder = stringResource(R.string.auth_password),
                keyboardType = KeyboardType.Password,
                isPassword = true,
            )

            TextButton(
                onClick = { onEvent(LoginScreenEvent.ForgotPasswordClicked) },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(
                    text = stringResource(R.string.auth_forgot_password),
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            AuthPrimaryButton(
                text = stringResource(R.string.auth_login),
                enabled = uiState.email.isNotBlank() && uiState.password.isNotBlank(),
                onClick = { onEvent(LoginScreenEvent.LoginClicked) },
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.BottomActionSpacing))

            AuthPrompt(
                message = stringResource(R.string.auth_login_prompt),
                action = stringResource(R.string.auth_signup),
                onClick = { onEvent(LoginScreenEvent.SignUpClicked) },
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
