package com.example.moil.feature.schedule.presentation

import androidx.compose.runtime.Composable

@Composable
fun AddScheduleScreen(
    uiState: AddScheduleUiState,
    onEvent: (AddScheduleScreenEvent) -> Unit,
) {
    AddScheduleScreenContent(
        uiState = uiState,
        onEvent = onEvent,
    )
}
