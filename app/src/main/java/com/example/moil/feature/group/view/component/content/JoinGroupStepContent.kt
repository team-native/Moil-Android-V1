package com.example.moil.feature.group.view

import androidx.compose.runtime.Composable
import com.example.moil.feature.group.viewmodel.JoinGroupScreenEvent
import com.example.moil.feature.group.viewmodel.JoinGroupStep
import com.example.moil.feature.group.viewmodel.JoinGroupUiState

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
