package com.example.moil.feature.auth.view

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
import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider
import com.example.moil.feature.auth.viewmodel.LoginScreenEvent
import com.example.moil.feature.auth.viewmodel.LoginUiState
import com.example.moil.feature.auth.viewmodel.emailRegex
import com.example.moil.feature.auth.viewmodel.passwordRegex
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

            val isEmailInvalid = uiState.email.isNotBlank() && !emailRegex.matches(uiState.email)
            val isPasswordTooShort = uiState.password.isNotBlank() && !passwordRegex.matches(uiState.password)

            AuthTextField(
                value = uiState.email,
                onValueChange = { email ->
                    onEvent(LoginScreenEvent.EmailChanged(email))
                },
                placeholder = stringResource(R.string.auth_email),
                isError = isEmailInvalid,
                keyboardType = KeyboardType.Email,
            )

            if (isEmailInvalid) {
                AuthErrorText(text = stringResource(R.string.auth_invalid_email))
            }

            Spacer(modifier = Modifier.height(MoilAuthDimension.FieldSpacing))

            AuthTextField(
                value = uiState.password,
                onValueChange = { password ->
                    onEvent(LoginScreenEvent.PasswordChanged(password))
                },
                placeholder = stringResource(R.string.auth_password),
                isError = isPasswordTooShort,
                keyboardType = KeyboardType.Password,
                isPassword = true,
            )

            if (isPasswordTooShort) {
                AuthErrorText(text = stringResource(R.string.auth_password_length_error))
            }

            TextButton(
                onClick = { onEvent(LoginScreenEvent.ForgotPasswordClicked) },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(
                    text = stringResource(R.string.auth_forgot_password),
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            uiState.errorMessage?.let { errorMessage ->
                AuthErrorText(text = errorMessage)
            }

            Spacer(modifier = Modifier.weight(1f))

            AuthPrimaryButton(
                text = stringResource(R.string.auth_login),
                enabled = uiState.canLogin(),
                onClick = { onEvent(LoginScreenEvent.LoginClicked) },
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.FieldSpacing))

            SocialLoginButton(
                provider = SocialLoginProvider.Google,
                enabled = !uiState.isLoading,
                onClick = {
                    onEvent(LoginScreenEvent.SocialLoginClicked(SocialLoginProvider.Google))
                },
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.FieldSpacing))

            SocialLoginButton(
                provider = SocialLoginProvider.Kakao,
                enabled = !uiState.isLoading,
                onClick = {
                    onEvent(LoginScreenEvent.SocialLoginClicked(SocialLoginProvider.Kakao))
                },
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.FieldSpacing))

            SocialLoginButton(
                provider = SocialLoginProvider.Apple,
                enabled = !uiState.isLoading,
                onClick = {
                    onEvent(LoginScreenEvent.SocialLoginClicked(SocialLoginProvider.Apple))
                },
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
