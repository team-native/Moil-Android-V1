package com.example.moil.feature.calendar.viewmodel

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
        scheduleDate = event.date,
        isGroupMenuVisible = false,
        isScheduleSheetVisible = true,
        scheduleSheetMode = CalendarScheduleSheetMode.List,
        selectedEventId = null,
        sharedMemberIds = sharedMemberIds.ifEmpty {
            members
                .firstOrNull { member -> member.isCurrentUser }
                ?.let { member -> setOf(member.id) }
                .orEmpty()
        },
    )
    CalendarScreenEvent.ScheduleSheetDismissed -> copy(
        isScheduleSheetVisible = false,
        scheduleSheetMode = CalendarScheduleSheetMode.List,
        selectedEventId = null,
    )
    CalendarScreenEvent.ScheduleCreateClicked -> copy(
        isScheduleSheetVisible = true,
        scheduleSheetMode = CalendarScheduleSheetMode.Create,
        selectedEventId = null,
        scheduleTitle = "",
        scheduleDate = selectedDate,
        scheduleStartTime = java.time.LocalTime.of(9, 0),
        scheduleEndTime = java.time.LocalTime.of(10, 0),
        scheduleLocation = "",
        scheduleMemo = "",
        sharedMemberIds = members
            .firstOrNull { member -> member.isCurrentUser }
            ?.let { member -> setOf(member.id) }
            .orEmpty(),
    )
    is CalendarScreenEvent.ScheduleItemClicked -> copy(
        isScheduleSheetVisible = true,
        scheduleSheetMode = CalendarScheduleSheetMode.Detail,
        selectedEventId = event.eventId,
    )
    CalendarScreenEvent.ScheduleDetailEditClicked -> copy(
        scheduleSheetMode = CalendarScheduleSheetMode.Edit,
    )
    CalendarScreenEvent.ScheduleEditCanceled -> copy(
        scheduleSheetMode = CalendarScheduleSheetMode.Detail,
    )
    CalendarScreenEvent.ScheduleDetailDeleteClicked,
    CalendarScreenEvent.ScheduleDeleteConfirmed -> this
    CalendarScreenEvent.ScheduleSaveClicked -> this
    is CalendarScreenEvent.ScheduleTitleChanged -> copy(scheduleTitle = event.title)
    CalendarScreenEvent.ScheduleDateClicked,
    CalendarScreenEvent.ScheduleTimeClicked,
    CalendarScreenEvent.ScheduleLocationClicked,
    CalendarScreenEvent.ScheduleMemoClicked -> this
    is CalendarScreenEvent.ScheduleDateChanged -> copy(scheduleDate = event.date)
    is CalendarScreenEvent.ScheduleTimeChanged -> copy(
        scheduleStartTime = event.time,
        scheduleEndTime = event.time.plusHours(1),
    )
    is CalendarScreenEvent.ScheduleLocationChanged -> copy(scheduleLocation = event.location)
    is CalendarScreenEvent.ScheduleMemoChanged -> copy(scheduleMemo = event.memo)
    is CalendarScreenEvent.SharedMemberClicked -> copy(
        sharedMemberIds = sharedMemberIds.toggle(event.memberId),
    )
}

private fun Set<Long>.toggle(memberId: Long): Set<Long> =
    if (memberId in this) this - memberId else this + memberId
