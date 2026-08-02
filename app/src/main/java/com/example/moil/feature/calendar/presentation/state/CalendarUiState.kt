package com.example.moil.feature.calendar.presentation

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import com.example.moil.core.domain.MoilError

data class CalendarUiState(
    val displayedMonth: YearMonth,
    val selectedDate: LocalDate,
    val groups: List<CalendarGroupUiModel> = emptyList(),
    val selectedGroupId: Long? = null,
    val members: List<CalendarMemberUiModel> = emptyList(),
    val eventsByDate: Map<LocalDate, List<CalendarEventUiModel>> = emptyMap(),
    val isGroupsLoading: Boolean = false,
    val groupLoadError: MoilError? = null,
    val isGroupMenuVisible: Boolean = false,
    val isScheduleSheetVisible: Boolean = false,
    val scheduleTitle: String = "",
    val isAllDay: Boolean = false,
    val scheduleTime: LocalTime = LocalTime.of(9, 0),
    val scheduleLocation: String = "",
    val sharedMemberIds: Set<Long> = emptySet(),
)
