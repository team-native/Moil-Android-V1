package com.example.moil.feature.group.presentation

import androidx.compose.runtime.Composable

@Composable
internal fun JoinGroupStepContent(
    uiState: JoinGroupUiState,
    onEvent: (JoinGroupScreenEvent) -> Unit,
) {
    when (uiState.step) {
        JoinGroupStep.InviteCode -> {
            JoinGroupScreenContent(
                uiState = uiState,
                onEvent = onEvent,
            )
        }

        JoinGroupStep.ProfileSetup -> {
            JoinGroupProfileSetupContent(
                uiState = uiState,
                onEvent = onEvent,
            )
        }
    }
}
