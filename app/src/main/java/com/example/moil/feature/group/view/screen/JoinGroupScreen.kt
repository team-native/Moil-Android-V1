package com.example.moil.feature.group.view

import androidx.compose.runtime.Composable
import com.example.moil.feature.group.viewmodel.JoinGroupScreenEvent
import com.example.moil.feature.group.viewmodel.JoinGroupUiState

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
