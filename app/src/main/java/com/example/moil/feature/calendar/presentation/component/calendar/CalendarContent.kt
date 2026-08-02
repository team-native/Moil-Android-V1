package com.example.moil.feature.calendar.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.moil.feature.calendar.presentation.CalendarScreenEvent
import com.example.moil.feature.calendar.presentation.CalendarUiState
import com.example.moil.ui.theme.MoilSpacing

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

@Composable
private fun CalendarGroupContent(
    uiState: CalendarUiState,
    onEvent: (CalendarScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        CalendarTopBarSection(
            selectedGroupName = uiState.groups
                .firstOrNull { group -> group.id == uiState.selectedGroupId }
                ?.name
                .orEmpty(),
            memberAvatarResources = uiState.members.map { member -> member.avatarRes },
            groups = uiState.groups,
            selectedGroupId = uiState.selectedGroupId,
            isGroupMenuVisible = uiState.isGroupMenuVisible,
            isScheduleSheetVisible = uiState.isScheduleSheetVisible,
            onGroupClick = { onEvent(CalendarScreenEvent.GroupMenuClicked) },
            onGroupSelected = { groupId -> onEvent(CalendarScreenEvent.GroupSelected(groupId)) },
        )

        Spacer(modifier = Modifier.height(MoilSpacing.ContentTop))

        CalendarMonthSection(
            displayedMonth = uiState.displayedMonth,
            isGroupMenuVisible = uiState.isGroupMenuVisible,
            isScheduleSheetVisible = uiState.isScheduleSheetVisible,
            onPreviousMonthClick = { onEvent(CalendarScreenEvent.PreviousMonthClicked) },
            onNextMonthClick = { onEvent(CalendarScreenEvent.NextMonthClicked) },
        )

        Spacer(modifier = Modifier.height(MoilSpacing.CalendarRow))

        if (uiState.isGroupMenuVisible) {
            Spacer(modifier = Modifier.weight(1f))
        } else {
            CalendarGrid(
                displayedMonth = uiState.displayedMonth,
                selectedDate = uiState.selectedDate,
                eventsByDate = uiState.eventsByDate,
                modifier = Modifier.weight(1f),
                onDateClick = { selectedDate ->
                    onEvent(CalendarScreenEvent.DateClicked(selectedDate))
                },
            )
        }
    }
}
