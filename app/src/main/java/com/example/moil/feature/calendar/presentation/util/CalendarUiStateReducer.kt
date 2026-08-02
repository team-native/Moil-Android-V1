package com.example.moil.feature.calendar.presentation

internal fun CalendarUiState.reduce(event: CalendarScreenEvent): CalendarUiState = when (event) {
    is CalendarScreenEvent.DestinationClicked -> this
    is CalendarScreenEvent.GroupSelected -> copy(
        selectedGroupId = event.groupId,
        isGroupMenuVisible = false,
        isScheduleSheetVisible = false,
        sharedMemberIds = emptySet(),
    )
    CalendarScreenEvent.EmptyGroupJoinClicked,
    CalendarScreenEvent.EmptyGroupCreateClicked,
    CalendarScreenEvent.RetryGroupsClicked -> this
    CalendarScreenEvent.PreviousMonthClicked -> copy(displayedMonth = displayedMonth.minusMonths(1))
    CalendarScreenEvent.NextMonthClicked -> copy(displayedMonth = displayedMonth.plusMonths(1))
    CalendarScreenEvent.GroupMenuClicked -> copy(isGroupMenuVisible = !isGroupMenuVisible)
    is CalendarScreenEvent.DateClicked -> copy(
        selectedDate = event.date,
        isGroupMenuVisible = false,
        isScheduleSheetVisible = true,
    )
    CalendarScreenEvent.ScheduleSheetDismissed -> copy(isScheduleSheetVisible = false)
    CalendarScreenEvent.ScheduleSaveClicked -> copy(isScheduleSheetVisible = false)
    is CalendarScreenEvent.ScheduleTitleChanged -> copy(scheduleTitle = event.title)
    CalendarScreenEvent.ScheduleDateClicked,
    CalendarScreenEvent.ScheduleTimeClicked,
    CalendarScreenEvent.ScheduleLocationClicked -> this
    is CalendarScreenEvent.ScheduleDateChanged -> copy(selectedDate = event.date)
    is CalendarScreenEvent.AllDayChanged -> copy(isAllDay = event.isAllDay)
    is CalendarScreenEvent.ScheduleTimeChanged -> copy(scheduleTime = event.time)
    is CalendarScreenEvent.ScheduleLocationChanged -> copy(scheduleLocation = event.location)
    is CalendarScreenEvent.SharedMemberClicked -> copy(
        sharedMemberIds = sharedMemberIds.toggle(event.memberId),
    )
}

private fun Set<Long>.toggle(memberId: Long): Set<Long> =
    if (memberId in this) this - memberId else this + memberId
