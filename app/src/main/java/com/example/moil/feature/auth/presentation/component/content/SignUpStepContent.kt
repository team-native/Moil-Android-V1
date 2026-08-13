package com.example.moil.feature.auth.presentation

import androidx.compose.runtime.Composable

@Composable
internal fun SignUpStepContent(
    uiState: SignUpUiState,
    onEvent: (SignUpScreenEvent) -> Unit,
) {
    when (uiState.currentStep) {
        SignUpStep.Information -> {
            SignUpInformationContent(
                uiState = uiState,
                onEvent = onEvent,
            )
        }

        SignUpStep.Verification -> {
            SignUpVerificationContent(
                uiState = uiState,
                onEvent = onEvent,
            )
        }

        SignUpStep.Password -> {
            SignUpPasswordContent(
                uiState = uiState,
                onEvent = onEvent,
            )
        }
    }
}
