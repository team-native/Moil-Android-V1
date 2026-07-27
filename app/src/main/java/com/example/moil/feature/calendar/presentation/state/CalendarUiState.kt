package com.example.moil.feature.calendar.presentation

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

data class CalendarUiState(
    val displayedMonth: YearMonth,
    val selectedDate: LocalDate,
    val isGroupMenuVisible: Boolean = false,
    val isScheduleSheetVisible: Boolean = false,
    val scheduleTitle: String = "",
    val isAllDay: Boolean = false,
    val scheduleTime: LocalTime = LocalTime.of(9, 0),
    val scheduleLocation: String = "",
    val sharedMemberIds: Set<CalendarMemberId> = emptySet(),
)
