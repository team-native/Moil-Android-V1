package com.example.moil.navigation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moil.feature.calendar.view.ScheduleSheetContent
import com.example.moil.feature.calendar.viewmodel.CalendarScreenEvent
import com.example.moil.feature.calendar.viewmodel.CalendarViewModel
import com.example.moil.feature.calendar.viewmodel.toCalendarScheduleUiModel
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.navigation.MoilMainUiState

/** 일정 목록·생성·수정·상세 바텀시트 목적지다. 시트 컨테이너는 overlay scene이 제공한다. */
@Composable
internal fun ScheduleSheetRoute(
    mainUiState: MoilMainUiState,
    groupViewModel: GroupViewModel,
    calendarViewModel: CalendarViewModel,
    onEvent: (CalendarScreenEvent) -> Unit,
) {
    val groupUiState by groupViewModel.uiState.collectAsStateWithLifecycle()
    val calendarRemoteUiState by calendarViewModel.uiState.collectAsStateWithLifecycle()
    val selectedSchedule = calendarRemoteUiState.selectedEvent?.let { selectedEvent ->
        runCatching {
            selectedEvent.toCalendarScheduleUiModel(
                fallbackProfileColor = groupUiState.selectedGroup?.myColor
                    ?: GroupColor.Unknown,
                groupMembers = groupUiState.members,
            )
        }.getOrNull()
    }

    ScheduleSheetContent(
        uiState = mainUiState.calendarUiState,
        selectedSchedule = selectedSchedule,
        isSelectedEventLoading = calendarRemoteUiState.isSelectedEventLoading,
        isMutationLoading = calendarRemoteUiState.isMutationLoading,
        hasSelectedEventError = calendarRemoteUiState.selectedEventError != null,
        hasMutationError = calendarRemoteUiState.mutationError != null,
        onEvent = onEvent,
    )
}
