package com.example.moil.feature.calendar.viewmodel

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import com.example.moil.core.domain.MoilError

enum class CalendarScheduleSheetMode {
    List,
    Create,
    Edit,
    Detail,
}

data class CalendarUiState(
    val displayedMonth: YearMonth,
    val selectedDate: LocalDate,
    val groups: List<CalendarGroupUiModel> = emptyList(),
    val selectedGroupId: Long? = null,
    val members: List<CalendarMemberUiModel> = emptyList(),
    val events: List<CalendarEventUiModel> = emptyList(),
    val schedules: List<CalendarScheduleUiModel> = emptyList(),
    val isGroupsLoading: Boolean = false,
    val groupLoadError: MoilError? = null,
    val isGroupMenuVisible: Boolean = false,
    val isScheduleSheetVisible: Boolean = false,
    val scheduleSheetMode: CalendarScheduleSheetMode = CalendarScheduleSheetMode.List,
    val selectedEventId: Long? = null,
    val scheduleTitle: String = "",
    val scheduleDate: LocalDate = selectedDate,
    val scheduleStartTime: LocalTime? = LocalTime.of(9, 0),
    val scheduleEndTime: LocalTime? = LocalTime.of(10, 0),
    val scheduleLocation: String = "",
    val scheduleMemo: String = "",
    val sharedMemberIds: Set<Long> = emptySet(),
)
