package com.example.moil.feature.auth.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
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
                .navigationBarsPadding()
                ,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // weight(1f)로 위쪽 남는 공간을 모두 차지해 아래 콘텐츠를 화면 하단으로 밀착시킵니다.
            Spacer(modifier = Modifier.weight(1f))

            AuthBranding()

            Spacer(modifier = Modifier.height(40.dp))

            LoginForm(uiState, onEvent, Modifier.widthIn(max = 440.dp))

            Spacer(modifier = Modifier.height(20.dp))

            AuthPrompt(
                message = stringResource(R.string.auth_login_prompt),
                action = stringResource(R.string.auth_signup),
                onClick = { onEvent(LoginScreenEvent.SignUpClicked) },
            )
        }
    }
}

@Composable
private fun LoginForm(
    uiState: LoginUiState,
    onEvent: (LoginScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEmailInvalid = uiState.email.isNotBlank() && !emailRegex.matches(uiState.email)
    val isPasswordTooShort = uiState.password.isNotBlank() && !passwordRegex.matches(uiState.password)

    Column(modifier = modifier) {
        AuthTextField(
            value = uiState.email,
            onValueChange = { onEvent(LoginScreenEvent.EmailChanged(it)) },
            placeholder = stringResource(R.string.auth_email),
            isError = isEmailInvalid,
            keyboardType = KeyboardType.Email,
        )
        if (isEmailInvalid) AuthErrorText(stringResource(R.string.auth_invalid_email))

        Spacer(modifier = Modifier.height(MoilAuthDimension.FieldSpacing))
        AuthTextField(
            value = uiState.password,
            onValueChange = { onEvent(LoginScreenEvent.PasswordChanged(it)) },
            placeholder = stringResource(R.string.auth_password),
            isError = isPasswordTooShort,
            keyboardType = KeyboardType.Password,
            isPassword = true,
        )
        if (isPasswordTooShort) AuthErrorText(stringResource(R.string.auth_password_length_error))

        TextButton(
            onClick = { onEvent(LoginScreenEvent.ForgotPasswordClicked) },
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(stringResource(R.string.auth_forgot_password), style = MaterialTheme.typography.labelMedium)
        }
        uiState.errorMessage?.let { errorMessage -> AuthErrorText(errorMessage) }

        Spacer(modifier = Modifier.height(46.dp))
        SocialLoginDivider()
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SocialLoginButton(SocialLoginProvider.Google, !uiState.isLoading) {
                onEvent(LoginScreenEvent.SocialLoginClicked(SocialLoginProvider.Google))
            }
            SocialLoginButton(SocialLoginProvider.Apple, !uiState.isLoading) {
                onEvent(LoginScreenEvent.SocialLoginClicked(SocialLoginProvider.Apple))
            }
            SocialLoginButton(SocialLoginProvider.Kakao, !uiState.isLoading) {
                onEvent(LoginScreenEvent.SocialLoginClicked(SocialLoginProvider.Kakao))
            }
        }
        Spacer(modifier = Modifier.height(45.dp))
        AuthPrimaryButton(
            text = stringResource(R.string.auth_login),
            enabled = uiState.canLogin() && !uiState.isLoading,
            onClick = { onEvent(LoginScreenEvent.LoginClicked) },
        )
    }
}

@Composable
private fun SocialLoginDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.auth_social_login),
            modifier = Modifier.padding(horizontal = 12.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}
