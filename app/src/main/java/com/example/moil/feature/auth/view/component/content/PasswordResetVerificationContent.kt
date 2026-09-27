package com.example.moil.feature.auth.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.core.component.toUserMessage
import com.example.moil.feature.auth.viewmodel.PASSWORD_RESET_CODE_LENGTH
import com.example.moil.feature.auth.viewmodel.PasswordResetScreenEvent
import com.example.moil.feature.auth.viewmodel.PasswordResetStep
import com.example.moil.feature.auth.viewmodel.PasswordResetUiState
import com.example.moil.ui.theme.MoilAuthDimension
import com.example.moil.ui.theme.MoilTheme

/** 비밀번호 찾기 2단계: 이메일로 받은 인증번호 6자리를 입력하고, 만료·재발송 시간을 안내한다. */
@Composable
internal fun PasswordResetVerificationContent(
    uiState: PasswordResetUiState,
    onEvent: (PasswordResetScreenEvent) -> Unit,
) {
    val resources = LocalContext.current.resources
    val countdown = rememberVerificationCountdown(uiState.codeIssuedAt)

    AuthScaffold(
        title = stringResource(R.string.auth_email_verification),
        canNavigateBack = true,
        onBackClick = { onEvent(PasswordResetScreenEvent.BackClicked) },
        bottomContent = {
            AuthPrimaryButton(
                text = stringResource(R.string.auth_next),
                enabled = uiState.canVerifyCode && !countdown.isExpired,
                onClick = { onEvent(PasswordResetScreenEvent.VerifyCodeClicked) },
            )
        },
    ) {
        Text(
            text = stringResource(R.string.auth_verification_description, uiState.email),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(modifier = Modifier.height(MoilAuthDimension.DescriptionBottomSpacing))

        AuthVerificationCodeField(
            value = uiState.verificationCode,
            onValueChange = { code ->
                onEvent(
                    PasswordResetScreenEvent.VerificationCodeChanged(
                        code.filter(Char::isDigit).take(PASSWORD_RESET_CODE_LENGTH),
                    ),
                )
            },
            isError = uiState.isVerificationValidationShown || countdown.isExpired,
        )

        if (uiState.isVerificationValidationShown && uiState.error == null) {
            AuthErrorText(text = stringResource(R.string.auth_verification_code_invalid))
        }

        uiState.error?.let { error ->
            AuthErrorText(text = error.toUserMessage(resources))
        }

        Spacer(modifier = Modifier.height(MoilAuthDimension.VerificationTimerTopSpacing))

        AuthVerificationTimer(
            countdown = countdown,
            isResendEnabled = !uiState.isLoading,
            onResendClick = { onEvent(PasswordResetScreenEvent.ResendCodeClicked) },
        )
    }
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun PasswordResetVerificationContentPreview() {
    MoilTheme(darkTheme = false) {
        PasswordResetVerificationContent(
            uiState = PasswordResetUiState(
                currentStep = PasswordResetStep.Verification,
                email = "moil@example.com",
                verificationCode = "12",
            ),
            onEvent = {},
        )
    }
}
