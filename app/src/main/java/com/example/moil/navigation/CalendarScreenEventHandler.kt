package com.example.moil.navigation

import com.example.moil.feature.calendar.viewmodel.CalendarScheduleSheetMode
import com.example.moil.feature.calendar.viewmodel.CalendarScreenEvent
import com.example.moil.feature.calendar.viewmodel.CalendarUiState
import com.example.moil.feature.calendar.viewmodel.CalendarViewModel
import com.example.moil.feature.calendar.viewmodel.reduce
import com.example.moil.feature.event.module.domain.model.EventMember
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.group.viewmodel.GroupViewModel
import java.time.LocalDate
import java.time.LocalTime

/**
 * 달력 화면과 일정 시트·다이얼로그가 공유하는 [CalendarScreenEvent] 분기다.
 *
 * 같은 이벤트가 달력 화면과 시트 양쪽에서 올라오기 때문에(예: 일정 저장은 시트에서, 그룹 선택은 달력에서)
 * 분기를 한 곳에 모아 두고 각 Route가 이 handler를 그대로 넘겨 쓴다.
 */
internal class CalendarScreenEventHandler(
    private val mainUiState: MoilMainUiState,
    private val groupViewModel: GroupViewModel,
    private val calendarViewModel: CalendarViewModel,
    private val navigator: MoilMainNavigator,
) {
    fun handle(event: CalendarScreenEvent) {
        when (event) {
            is CalendarScreenEvent.DestinationClicked -> {
                navigator.navigateToTab(event.destination.toMainDestination())
            }

            is CalendarScreenEvent.GroupSelected -> {
                mainUiState.calendarUiState = mainUiState.calendarUiState.reduce(event)
                navigator.close(MoilMainDestination.ScheduleSheet)
                groupViewModel.selectGroup(event.groupId)
            }

            CalendarScreenEvent.EmptyGroupJoinClicked -> {
                navigator.navigateToTab(MoilMainDestination.JoinGroup)
            }

            CalendarScreenEvent.EmptyGroupCreateClicked -> {
                navigator.push(MoilMainDestination.CreateGroup)
            }

            CalendarScreenEvent.RetryGroupsClicked -> groupViewModel.loadGroups()

            is CalendarScreenEvent.DateClicked -> {
                mainUiState.calendarUiState = mainUiState.calendarUiState.reduce(event)
                navigator.pushIfAbsent(MoilMainDestination.ScheduleSheet)
            }

            CalendarScreenEvent.ScheduleCreateClicked -> {
                mainUiState.calendarUiState = mainUiState.calendarUiState.reduce(event)
                navigator.pushIfAbsent(MoilMainDestination.ScheduleSheet)
            }

            is CalendarScreenEvent.ScheduleItemClicked -> {
                mainUiState.calendarUiState = mainUiState.calendarUiState.reduce(event)
                navigator.pushIfAbsent(MoilMainDestination.ScheduleSheet)
                calendarViewModel.loadEvent(event.eventId)
            }

            CalendarScreenEvent.ScheduleDateClicked -> {
                navigator.push(MoilMainDestination.ScheduleDatePicker)
            }

            CalendarScreenEvent.ScheduleTimeClicked -> {
                navigator.push(MoilMainDestination.ScheduleTimePicker)
            }

            CalendarScreenEvent.ScheduleLocationClicked -> {
                navigator.push(MoilMainDestination.ScheduleLocation)
            }

            CalendarScreenEvent.ScheduleMemoClicked -> {
                navigator.push(MoilMainDestination.ScheduleMemo)
            }

            CalendarScreenEvent.ScheduleDetailEditClicked -> {
                val selectedEvent = calendarViewModel.uiState.value.selectedEvent

                if (selectedEvent != null) {
                    mainUiState.calendarUiState = mainUiState.calendarUiState
                        .reduce(event)
                        .copy(
                            scheduleTitle = selectedEvent.title,
                            scheduleDate = LocalDate.parse(selectedEvent.date),
                            scheduleStartTime = selectedEvent.startTime?.let(LocalTime::parse),
                            scheduleEndTime = selectedEvent.endTime?.let(LocalTime::parse),
                            scheduleLocation = selectedEvent.location.orEmpty(),
                            scheduleMemo = selectedEvent.memo.orEmpty(),
                            sharedMemberIds = selectedEvent.members
                                .map { member -> member.userId }
                                .toSet(),
                        )
                }
            }

            CalendarScreenEvent.ScheduleDetailDeleteClicked -> {
                navigator.push(MoilMainDestination.ScheduleDeleteConfirmation)
            }

            CalendarScreenEvent.ScheduleSheetDismissed -> {
                mainUiState.calendarUiState = mainUiState.calendarUiState.reduce(event)
                navigator.close(MoilMainDestination.ScheduleSheet)
            }

            CalendarScreenEvent.ScheduleSaveClicked -> saveSchedule()

            CalendarScreenEvent.ScheduleDeleteConfirmed -> {
                val selectedEventId = mainUiState.calendarUiState.selectedEventId

                if (selectedEventId != null) {
                    navigator.close(MoilMainDestination.ScheduleDeleteConfirmation)
                    calendarViewModel.deleteEvent(selectedEventId)
                }
            }

            CalendarScreenEvent.PreviousMonthClicked,
            CalendarScreenEvent.NextMonthClicked -> {
                mainUiState.calendarUiState = mainUiState.calendarUiState.reduce(event)
                calendarViewModel.selectMonth(mainUiState.calendarUiState.displayedMonth)
            }

            else -> mainUiState.calendarUiState = mainUiState.calendarUiState.reduce(event)
        }
    }

    // 일정 시트의 저장 버튼에서 호출되어 생성/수정 요청을 만든다.
    // 공유 대상이 비어 있으면 현재 사용자만 공유 대상으로 삼아 서버 요청이 빈 목록으로 나가지 않게 한다.
    private fun saveSchedule() {
        val calendarUiState = mainUiState.calendarUiState
        val selectedGroupId = calendarUiState.selectedGroupId

        if (selectedGroupId == null || calendarUiState.scheduleTitle.isBlank()) {
            return
        }

        val selectedSharedMembers = calendarUiState.members
            .filter { member -> member.id in calendarUiState.sharedMemberIds }
        val sharedMembers = selectedSharedMembers.ifEmpty {
            calendarUiState.members.filter { member -> member.isCurrentUser }
        }
        val sharedMemberIds = sharedMembers.map { member -> member.id }
        val eventMembers = sharedMembers.map { member ->
            EventMember(
                userId = member.id,
                nickname = member.name,
                color = member.color,
            )
        }
        val selectedEventId = calendarUiState.selectedEventId
        val isEditingExistingSchedule =
            calendarUiState.scheduleSheetMode == CalendarScheduleSheetMode.Edit &&
                selectedEventId != null

        if (isEditingExistingSchedule) {
            calendarViewModel.updateEvent(
                eventId = requireNotNull(selectedEventId),
                event = calendarUiState.toGroupEvent(
                    eventId = selectedEventId,
                    members = eventMembers,
                ),
                sharedMemberIds = sharedMemberIds,
            )
        } else {
            calendarViewModel.createEvent(
                event = calendarUiState.toGroupEvent(
                    eventId = NEW_EVENT_ID,
                    members = eventMembers,
                ),
                sharedMemberIds = sharedMemberIds,
            )
        }
    }

    private companion object {
        /** 아직 서버에 생성되지 않은 일정을 만들 때 쓰는 eventId 자리값이다. */
        const val NEW_EVENT_ID = 0L
    }
}

private fun CalendarUiState.toGroupEvent(
    eventId: Long,
    members: List<EventMember>,
): GroupEvent = GroupEvent(
    id = eventId,
    title = scheduleTitle.trim(),
    date = scheduleDate.toString(),
    startTime = scheduleStartTime?.toString(),
    endTime = scheduleEndTime?.toString(),
    location = scheduleLocation.ifBlank { null },
    memo = scheduleMemo.ifBlank { null },
    members = members,
)
