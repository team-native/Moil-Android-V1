package com.example.moil.feature.calendar.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.moil.feature.calendar.viewmodel.CalendarScreenEvent
import com.example.moil.feature.calendar.viewmodel.CalendarUiState
import com.example.moil.ui.theme.MoilCalendarDimension

@Composable
internal fun CalendarGroupContent(
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

        Spacer(modifier = Modifier.height(MoilCalendarDimension.MonthHeaderVerticalSpacing))

        CalendarMonthSection(
            displayedMonth = uiState.displayedMonth,
            isGroupMenuVisible = uiState.isGroupMenuVisible,
            isScheduleSheetVisible = uiState.isScheduleSheetVisible,
            onPreviousMonthClick = { onEvent(CalendarScreenEvent.PreviousMonthClicked) },
            onNextMonthClick = { onEvent(CalendarScreenEvent.NextMonthClicked) },
        )

        Spacer(modifier = Modifier.height(MoilCalendarDimension.MonthHeaderVerticalSpacing))

        if (uiState.isGroupMenuVisible) {
            Spacer(modifier = Modifier.weight(1f))
        } else {
            CalendarGrid(
                displayedMonth = uiState.displayedMonth,
                events = uiState.events,
                modifier = Modifier.weight(1f),
                onDateClick = { selectedDate ->
                    onEvent(CalendarScreenEvent.DateClicked(selectedDate))
                },
            )
        }
    }
}
