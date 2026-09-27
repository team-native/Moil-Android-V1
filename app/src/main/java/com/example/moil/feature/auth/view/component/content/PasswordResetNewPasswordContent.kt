package com.example.moil.feature.auth.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.core.component.toUserMessage
import com.example.moil.feature.auth.viewmodel.PasswordResetScreenEvent
import com.example.moil.feature.auth.viewmodel.PasswordResetStep
import com.example.moil.feature.auth.viewmodel.PasswordResetUiState
import com.example.moil.feature.auth.viewmodel.passwordRegex
import com.example.moil.ui.theme.MoilAuthDimension
import com.example.moil.ui.theme.MoilTheme

/** 비밀번호 찾기 3단계: 인증된 세션으로 새 비밀번호를 설정한다. */
@Composable
internal fun PasswordResetNewPasswordContent(
    uiState: PasswordResetUiState,
    onEvent: (PasswordResetScreenEvent) -> Unit,
) {
    val resources = LocalResources.current
    val isPasswordTooShort = uiState.password.isNotEmpty() && !passwordRegex.matches(uiState.password)
    val isPasswordConfirmationInvalid = uiState.passwordConfirmation.isNotEmpty() &&
        uiState.password != uiState.passwordConfirmation

    AuthScaffold(
        title = stringResource(R.string.auth_password_reset_new_password_title),
        canNavigateBack = true,
        onBackClick = { onEvent(PasswordResetScreenEvent.BackClicked) },
        bottomContent = {
            AuthPrimaryButton(
                text = stringResource(R.string.auth_password_reset_confirm),
                enabled = uiState.canResetPassword,
                onClick = { onEvent(PasswordResetScreenEvent.ResetPasswordClicked) },
            )
        },
    ) {
        AuthFieldLabel(text = stringResource(R.string.auth_password_reset_new_password))
        AuthTextField(
            value = uiState.password,
            onValueChange = { password ->
                onEvent(PasswordResetScreenEvent.PasswordChanged(password))
            },
            placeholder = stringResource(R.string.auth_password_reset_new_password),
            isError = isPasswordTooShort,
            isPassword = true,
        )

        if (isPasswordTooShort) {
            AuthErrorText(text = stringResource(R.string.auth_password_length_error))
        }

        Spacer(modifier = Modifier.height(MoilAuthDimension.SectionSpacing))

        AuthFieldLabel(text = stringResource(R.string.auth_password_reset_new_password_confirm))
        AuthTextField(
            value = uiState.passwordConfirmation,
            onValueChange = { passwordConfirmation ->
                onEvent(PasswordResetScreenEvent.PasswordConfirmationChanged(passwordConfirmation))
            },
            placeholder = stringResource(R.string.auth_confirm_password_placeholder),
            isError = isPasswordConfirmationInvalid,
            isPassword = true,
        )

        if (isPasswordConfirmationInvalid) {
            AuthErrorText(text = stringResource(R.string.auth_password_mismatch_error))
        }

        uiState.error?.let { error ->
            AuthErrorText(text = error.toUserMessage(resources))
        }
    }
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun PasswordResetNewPasswordContentPreview() {
    MoilTheme(darkTheme = true) {
        PasswordResetNewPasswordContent(
            uiState = PasswordResetUiState(
                currentStep = PasswordResetStep.NewPassword,
                password = "1234",
            ),
            onEvent = {},
        )
    }
}
