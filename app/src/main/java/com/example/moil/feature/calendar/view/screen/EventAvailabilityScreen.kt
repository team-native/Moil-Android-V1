package com.example.moil.feature.calendar.view

import androidx.compose.runtime.Composable
import com.example.moil.feature.calendar.viewmodel.EventAvailabilityDisplayModel
import com.example.moil.feature.calendar.viewmodel.EventAvailabilityScreenEvent
import com.example.moil.feature.calendar.viewmodel.EventAvailabilityUiState

/** 일정 날짜의 가능 시간 화면이다. */
@Composable
fun EventAvailabilityScreen(
    uiState: EventAvailabilityUiState,
    displayModel: EventAvailabilityDisplayModel,
    onEvent: (EventAvailabilityScreenEvent) -> Unit,
) {
    EventAvailabilityScreenContent(
        uiState = uiState,
        displayModel = displayModel,
        onEvent = onEvent,
    )
}
