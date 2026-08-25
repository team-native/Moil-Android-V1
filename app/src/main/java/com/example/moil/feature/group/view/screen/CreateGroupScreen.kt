package com.example.moil.feature.group.view

import androidx.compose.runtime.Composable
import com.example.moil.feature.group.viewmodel.CreateGroupScreenEvent
import com.example.moil.feature.group.viewmodel.CreateGroupUiState

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
