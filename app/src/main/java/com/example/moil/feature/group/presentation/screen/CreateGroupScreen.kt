package com.example.moil.feature.group.presentation

import androidx.compose.runtime.Composable

@Composable
fun CreateGroupScreen(
    uiState: CreateGroupUiState,
    onEvent: (CreateGroupScreenEvent) -> Unit,
) {
    CreateGroupScreenContent(
        uiState = uiState,
        onEvent = onEvent,
    )
}
