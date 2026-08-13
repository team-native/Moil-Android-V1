package com.example.moil.feature.group.presentation

import androidx.compose.runtime.Composable

@Composable
fun JoinGroupScreen(
    uiState: JoinGroupUiState,
    onEvent: (JoinGroupScreenEvent) -> Unit,
) {
    JoinGroupStepContent(
        uiState = uiState,
        onEvent = onEvent,
    )
}
