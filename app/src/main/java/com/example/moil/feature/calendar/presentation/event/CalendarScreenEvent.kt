package com.example.moil.feature.calendar.presentation

import com.example.moil.core.component.MoilNavigationDestination
import java.time.LocalDate
import java.time.LocalTime

sealed interface CalendarScreenEvent {
    data class DestinationClicked(val destination: MoilNavigationDestination) : CalendarScreenEvent
    data object PreviousMonthClicked : CalendarScreenEvent
    data object NextMonthClicked : CalendarScreenEvent
    data object GroupMenuClicked : CalendarScreenEvent
    data class GroupSelected(val groupId: Long) : CalendarScreenEvent
    data object EmptyGroupJoinClicked : CalendarScreenEvent
    data object EmptyGroupCreateClicked : CalendarScreenEvent
    data object RetryGroupsClicked : CalendarScreenEvent
    data class DateClicked(val date: LocalDate) : CalendarScreenEvent
    data object ScheduleSheetDismissed : CalendarScreenEvent
    data object ScheduleSaveClicked : CalendarScreenEvent
    data class ScheduleTitleChanged(val title: String) : CalendarScreenEvent
    data object ScheduleStartDateClicked : CalendarScreenEvent
    data class ScheduleStartDateChanged(val date: LocalDate) : CalendarScreenEvent
    data object ScheduleEndDateClicked : CalendarScreenEvent
    data class ScheduleEndDateChanged(val date: LocalDate) : CalendarScreenEvent
    data class AllDayChanged(val isAllDay: Boolean) : CalendarScreenEvent
    data object ScheduleTimeClicked : CalendarScreenEvent
    data class ScheduleTimeChanged(val time: LocalTime) : CalendarScreenEvent
    data object ScheduleLocationClicked : CalendarScreenEvent
    data class ScheduleLocationChanged(val location: String) : CalendarScreenEvent
    data class SharedMemberClicked(val memberId: Long) : CalendarScreenEvent
}
