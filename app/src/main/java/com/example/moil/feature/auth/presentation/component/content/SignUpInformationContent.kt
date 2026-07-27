package com.example.moil.feature.auth.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.ui.theme.MoilAuthDimension

@Composable
internal fun SignUpInformationContent(
    uiState: SignUpUiState,
    onEvent: (SignUpScreenEvent) -> Unit,
) {
    AuthScaffold(
        title = stringResource(R.string.auth_signup),
        canNavigateBack = false,
        onBackClick = { onEvent(SignUpScreenEvent.BackClicked) },
        bottomContent = {
            AuthPrimaryButton(
                text = stringResource(R.string.auth_next),
                enabled = uiState.name.isNotBlank() && uiState.emailRegexMatches(),
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
        AuthFieldLabel(text = stringResource(R.string.auth_name))
        AuthTextField(
            value = uiState.name,
            onValueChange = { name ->
                onEvent(SignUpScreenEvent.NameChanged(name))
            },
            placeholder = stringResource(R.string.auth_name_placeholder),
        )

        Spacer(modifier = Modifier.height(18.dp))

        AuthFieldLabel(text = stringResource(R.string.auth_email))
        AuthTextField(
            value = uiState.email,
            onValueChange = { email ->
                onEvent(SignUpScreenEvent.EmailChanged(email))
            },
            placeholder = stringResource(R.string.auth_email_placeholder),
            isError = uiState.email.isNotBlank() && !uiState.emailRegexMatches(),
            keyboardType = KeyboardType.Email,
        )
        if (uiState.email.isNotBlank() && !uiState.emailRegexMatches()) {
            AuthErrorText(text = stringResource(R.string.auth_invalid_email))
        }
    }
}
