package com.example.moil.feature.auth.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.ui.theme.MoilAuthDimension

@Composable
internal fun SignUpVerificationContent(
    uiState: SignUpUiState,
    onEvent: (SignUpScreenEvent) -> Unit,
) {
    AuthScaffold(
        title = stringResource(R.string.auth_email_verification),
        canNavigateBack = true,
        onBackClick = { onEvent(SignUpScreenEvent.BackClicked) },
        bottomContent = {
            AuthPrimaryButton(
                text = stringResource(R.string.auth_next),
                enabled = true,
                onClick = { onEvent(SignUpScreenEvent.NextClicked) },
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.BottomActionSpacing))

            AuthPrompt(
                message = stringResource(R.string.auth_signup_prompt),
                action = stringResource(R.string.auth_login),
                onClick = { onEvent(SignUpScreenEvent.LoginClicked) },
            )
        },
    ) {
        Text(
            text = stringResource(R.string.auth_verification_description, uiState.email),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(modifier = Modifier.height(28.dp))

        AuthVerificationCodeField(
            value = uiState.verificationCode,
            onValueChange = { code ->
                onEvent(
                    SignUpScreenEvent.VerificationCodeChanged(
                        code.filter(Char::isDigit).take(verificationCodeLength),
                    ),
                )
            },
            isError = uiState.isVerificationValidationShown,
        )

        if (uiState.isVerificationValidationShown) {
            AuthErrorText(text = stringResource(R.string.auth_verification_code_invalid))
        }

        uiState.errorMessage?.let { errorMessage ->
            AuthErrorText(text = errorMessage)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.auth_resend_verification),
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
