package com.example.moil.feature.schedule.view

import androidx.compose.runtime.Composable
import com.example.moil.feature.schedule.viewmodel.AddScheduleScreenEvent
import com.example.moil.feature.schedule.viewmodel.AddScheduleUiState

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
