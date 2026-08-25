package com.example.moil.feature.auth.view

import androidx.compose.runtime.Composable
import com.example.moil.feature.auth.viewmodel.SignUpScreenEvent
import com.example.moil.feature.auth.viewmodel.SignUpStep
import com.example.moil.feature.auth.viewmodel.SignUpUiState

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
