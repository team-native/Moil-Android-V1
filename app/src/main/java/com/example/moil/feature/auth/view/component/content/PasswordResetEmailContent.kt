package com.example.moil.feature.auth.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.core.component.toUserMessage
import com.example.moil.feature.auth.viewmodel.PasswordResetScreenEvent
import com.example.moil.feature.auth.viewmodel.PasswordResetUiState
import com.example.moil.feature.auth.viewmodel.emailRegex
import com.example.moil.ui.theme.MoilAuthDimension
import com.example.moil.ui.theme.MoilTheme

/** 비밀번호 찾기 1단계: 가입한 이메일을 입력받아 인증번호를 요청한다. */
@Composable
internal fun PasswordResetEmailContent(
    uiState: PasswordResetUiState,
    onEvent: (PasswordResetScreenEvent) -> Unit,
) {
    val resources = LocalContext.current.resources
    val isEmailInvalid = uiState.email.isNotEmpty() && !emailRegex.matches(uiState.email)

    AuthScaffold(
        title = stringResource(R.string.auth_password_reset_title),
        canNavigateBack = true,
        onBackClick = { onEvent(PasswordResetScreenEvent.BackClicked) },
        bottomContent = {
            AuthPrimaryButton(
                text = stringResource(R.string.auth_password_reset_send_code),
                enabled = uiState.canSendCode,
                onClick = { onEvent(PasswordResetScreenEvent.SendCodeClicked) },
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.BottomActionSpacing))

            AuthPrompt(
                message = stringResource(R.string.auth_password_reset_prompt),
                action = stringResource(R.string.auth_login),
                onClick = { onEvent(PasswordResetScreenEvent.LoginClicked) },
            )
        },
    ) {
        Text(
            text = stringResource(R.string.auth_password_reset_description),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(modifier = Modifier.height(MoilAuthDimension.DescriptionBottomSpacing))

        AuthFieldLabel(text = stringResource(R.string.auth_email))
        AuthTextField(
            value = uiState.email,
            onValueChange = { email ->
                onEvent(PasswordResetScreenEvent.EmailChanged(email))
            },
            placeholder = stringResource(R.string.auth_email_placeholder),
            isError = isEmailInvalid,
            keyboardType = KeyboardType.Email,
        )

        if (isEmailInvalid) {
            AuthErrorText(text = stringResource(R.string.auth_invalid_email))
        }

        uiState.error?.let { error ->
            AuthErrorText(text = error.toUserMessage(resources))
        }
    }
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun PasswordResetEmailContentPreview() {
    MoilTheme(darkTheme = false) {
        PasswordResetEmailContent(
            uiState = PasswordResetUiState(email = "moil@example.com"),
            onEvent = {},
        )
    }
}
