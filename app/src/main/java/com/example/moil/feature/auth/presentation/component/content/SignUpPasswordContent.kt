package com.example.moil.feature.auth.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.ui.theme.MoilAuthDimension

@Composable
internal fun SignUpPasswordContent(
    uiState: SignUpUiState,
    onEvent: (SignUpScreenEvent) -> Unit,
) {
    val isPasswordTooShort = uiState.password.isNotEmpty() && !passwordRegex.matches(uiState.password)
    val isPasswordConfirmationInvalid = uiState.passwordConfirmation.isNotEmpty() &&
        uiState.password != uiState.passwordConfirmation

    AuthScaffold(
        title = stringResource(R.string.auth_password_setup),
        canNavigateBack = true,
        onBackClick = { onEvent(SignUpScreenEvent.BackClicked) },
        bottomContent = {
            AuthPrimaryButton(
                text = stringResource(R.string.auth_create_account),
                enabled = uiState.canCreateAccount(),
                onClick = { onEvent(SignUpScreenEvent.CreateAccountClicked) },
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.BottomActionSpacing))

            AuthPrompt(
                message = stringResource(R.string.auth_signup_prompt),
                action = stringResource(R.string.auth_login),
                onClick = { onEvent(SignUpScreenEvent.LoginClicked) },
            )
        },
    ) {
        AuthFieldLabel(text = stringResource(R.string.auth_password))
        AuthTextField(
            value = uiState.password,
            onValueChange = { password ->
                onEvent(SignUpScreenEvent.PasswordChanged(password))
            },
            placeholder = stringResource(R.string.auth_password),
            isError = isPasswordTooShort,
            isPassword = true,
        )
        if (isPasswordTooShort) {
            AuthErrorText(text = stringResource(R.string.auth_password_length_error))
        }

        Spacer(modifier = Modifier.height(18.dp))

        AuthFieldLabel(text = stringResource(R.string.auth_confirm_password))
        AuthTextField(
            value = uiState.passwordConfirmation,
            onValueChange = { passwordConfirmation ->
                onEvent(SignUpScreenEvent.PasswordConfirmationChanged(passwordConfirmation))
            },
            placeholder = stringResource(R.string.auth_confirm_password_placeholder),
            isError = isPasswordConfirmationInvalid,
            isPassword = true,
        )
        if (isPasswordConfirmationInvalid) {
            AuthErrorText(text = stringResource(R.string.auth_password_mismatch_error))
        }

        uiState.errorMessage?.let { errorMessage ->
            AuthErrorText(text = errorMessage)
        }
    }
}
