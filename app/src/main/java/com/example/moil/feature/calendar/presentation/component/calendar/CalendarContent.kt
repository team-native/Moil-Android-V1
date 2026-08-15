package com.example.moil.feature.calendar.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.moil.feature.calendar.presentation.CalendarScreenEvent
import com.example.moil.feature.calendar.presentation.CalendarUiState

@Composable
internal fun CalendarContent(
    uiState: CalendarUiState,
    onEvent: (CalendarScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        uiState.isGroupsLoading -> CalendarGroupLoadingContent(modifier = modifier)
        uiState.groupLoadError != null -> CalendarGroupErrorContent(
            onRetryClick = { onEvent(CalendarScreenEvent.RetryGroupsClicked) },
            modifier = modifier,
        )
        uiState.groups.isEmpty() -> CalendarEmptyGroupContent(
            onJoinGroupClick = { onEvent(CalendarScreenEvent.EmptyGroupJoinClicked) },
            onCreateGroupClick = { onEvent(CalendarScreenEvent.EmptyGroupCreateClicked) },
            modifier = modifier,
        )
        else -> CalendarGroupContent(
            uiState = uiState,
            onEvent = onEvent,
            modifier = modifier,
        )
    }
}
