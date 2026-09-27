package com.example.moil.feature.calendar.viewmodel

import com.example.moil.feature.event.module.domain.model.EventAvailabilityTimeSlot
import com.example.moil.feature.event.module.domain.model.EventMemberAvailability
import com.example.moil.feature.group.module.domain.model.GroupColor
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/** 가능 시간 격자의 시작·끝 시각과 칸 크기다. 서버 규칙이 아닌 화면 표시 범위다. */
val AVAILABILITY_GRID_START: LocalTime = LocalTime.of(9, 0)
val AVAILABILITY_GRID_END: LocalTime = LocalTime.of(22, 0)
const val AVAILABILITY_SLOT_MINUTES = 30L

private val serverTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/** 격자에 표시할 30분 칸의 시작 시각 목록이다. */
fun availabilityGridSlotStarts(): List<LocalTime> = generateSequence(AVAILABILITY_GRID_START) { slotStart ->
    slotStart.plusMinutes(AVAILABILITY_SLOT_MINUTES)
}.takeWhile { slotStart -> slotStart < AVAILABILITY_GRID_END }
    .toList()

/** 서버 시각 문자열("HH:mm" 또는 "HH:mm:ss")을 읽는다. 읽을 수 없으면 null로 두어 화면이 종료되지 않게 한다. */
fun String.toAvailabilityTimeOrNull(): LocalTime? = try {
    LocalTime.parse(this)
} catch (_: DateTimeParseException) {
    null
}

/** 서버에 보낼 "HH:mm" 문자열로 바꾼다. */
fun LocalTime.toAvailabilityServerTime(): String = format(serverTimeFormatter)

/**
 * 서버의 자유 형식 시간대를 격자 칸 선택으로 바꾼다. 칸과 조금이라도 겹치면 선택된 것으로 본다.
 * 다른 기기에서 분 단위로 입력한 값은 30분 칸 단위로 넓혀 표시된다.
 */
fun List<EventAvailabilityTimeSlot>.toSlotStarts(): Set<LocalTime> {
    val parsedRanges = mapNotNull { timeSlot -> timeSlot.toTimeRangeOrNull() }

    return availabilityGridSlotStarts()
        .filter { slotStart ->
            val slotEnd = slotStart.plusMinutes(AVAILABILITY_SLOT_MINUTES)

            parsedRanges.any { (rangeStart, rangeEnd) -> rangeStart < slotEnd && slotStart < rangeEnd }
        }
        .toSet()
}

/** 선택한 칸 중 이어지는 칸을 하나의 시간대로 합쳐 서버 요청 형식으로 만든다. */
fun Set<LocalTime>.toAvailabilityTimeSlots(): List<EventAvailabilityTimeSlot> {
    val sortedStarts = sorted()
    val mergedRanges = mutableListOf<Pair<LocalTime, LocalTime>>()

    sortedStarts.forEach { slotStart ->
        val slotEnd = slotStart.plusMinutes(AVAILABILITY_SLOT_MINUTES)
        val lastRange = mergedRanges.lastOrNull()

        if (lastRange != null && lastRange.second == slotStart) {
            mergedRanges[mergedRanges.lastIndex] = lastRange.first to slotEnd
        } else {
            mergedRanges += slotStart to slotEnd
        }
    }

    return mergedRanges.map { (rangeStart, rangeEnd) ->
        EventAvailabilityTimeSlot(
            startTime = rangeStart.toAvailabilityServerTime(),
            endTime = rangeEnd.toAvailabilityServerTime(),
        )
    }
}

/**
 * 참여자별 가능 시간을 30분 칸별 가능 인원으로 집계한다.
 * [participantCount]명 모두 가능한 칸은 모두 가능으로 표시한다.
 */
fun List<EventMemberAvailability>.toAvailabilityCells(participantCount: Int): List<EventAvailabilityCellUiModel> {
    val memberRanges = map { memberAvailability ->
        memberAvailability to memberAvailability.timeSlots.mapNotNull { timeSlot -> timeSlot.toTimeRangeOrNull() }
    }

    return availabilityGridSlotStarts().map { slotStart ->
        val slotEnd = slotStart.plusMinutes(AVAILABILITY_SLOT_MINUTES)
        val availableMembers = memberRanges
            .filter { (_, ranges) ->
                ranges.any { (rangeStart, rangeEnd) -> rangeStart < slotEnd && slotStart < rangeEnd }
            }
            .map { (memberAvailability, _) ->
                EventAvailabilityMemberUiModel(
                    userId = memberAvailability.userId,
                    nickname = memberAvailability.nickname,
                    color = memberAvailability.color ?: GroupColor.Unknown,
                )
            }

        EventAvailabilityCellUiModel(
            startTime = slotStart,
            endTime = slotEnd,
            availableCount = availableMembers.size,
            isAvailableForEveryone = participantCount > 0 && availableMembers.size >= participantCount,
            availableMembers = availableMembers,
        )
    }
}

/** 이어지는 칸 중 가능 인원이 가장 많은 첫 구간을 찾는다. 아무도 가능하지 않으면 null이다. */
fun List<EventAvailabilityCellUiModel>.toBestRange(): EventAvailabilityRangeUiModel? {
    val maxCount = maxOfOrNull(EventAvailabilityCellUiModel::availableCount) ?: return null

    if (maxCount == 0) {
        return null
    }

    val firstBestIndex = indexOfFirst { cell -> cell.availableCount == maxCount }
    var lastBestIndex = firstBestIndex

    while (lastBestIndex + 1 < size && this[lastBestIndex + 1].availableCount == maxCount) {
        lastBestIndex += 1
    }

    return EventAvailabilityRangeUiModel(
        startTime = this[firstBestIndex].startTime,
        endTime = this[lastBestIndex].endTime,
        availableCount = maxCount,
    )
}

private fun EventAvailabilityTimeSlot.toTimeRangeOrNull(): Pair<LocalTime, LocalTime>? {
    val rangeStart = startTime.toAvailabilityTimeOrNull() ?: return null
    val rangeEnd = endTime.toAvailabilityTimeOrNull() ?: return null

    return (rangeStart to rangeEnd).takeIf { rangeStart < rangeEnd }
}

/** 선택한 일정의 참석 현황을 상세 시트 표시 모델로 바꾼다. 다른 일정의 상태가 남아 있으면 로딩으로 본다. */
fun CalendarRemoteUiState.toEventAttendanceUiModel(eventId: Long): EventAttendanceUiModel {
    if (attendanceEventId != eventId) {
        return EventAttendanceUiModel()
    }

    return when (val attendanceState = attendance) {
        CalendarRemoteLoadState.Idle,
        CalendarRemoteLoadState.Loading,
        -> EventAttendanceUiModel(
            isMutating = isAttendanceMutationLoading,
            mutationError = attendanceMutationError,
        )

        is CalendarRemoteLoadState.Failure -> EventAttendanceUiModel(
            loadState = EventSectionLoadState.Failed,
            isMutating = isAttendanceMutationLoading,
            mutationError = attendanceMutationError,
        )

        is CalendarRemoteLoadState.Success -> {
            val eventAttendance = attendanceState.value
            val pendingCount = (
                eventAttendance.participantCount -
                    eventAttendance.attendingCount -
                    eventAttendance.declinedCount
                ).coerceAtLeast(0)

            EventAttendanceUiModel(
                loadState = EventSectionLoadState.Loaded,
                myStatus = eventAttendance.myStatus,
                attendingCount = eventAttendance.attendingCount,
                declinedCount = eventAttendance.declinedCount,
                pendingCount = pendingCount,
                isMutating = isAttendanceMutationLoading,
                mutationError = attendanceMutationError,
            )
        }
    }
}

/** 선택한 일정·날짜의 가능 시간 응답을 격자와 요약 표시 모델로 바꾼다. */
fun CalendarRemoteUiState.toEventAvailabilityDisplayModel(
    eventId: Long,
    date: String,
): EventAvailabilityDisplayModel {
    if (availabilityEventId != eventId || availabilityDate != date) {
        return EventAvailabilityDisplayModel()
    }

    val memberState = memberAvailability
    val summaryState = availabilitySummary
    val myState = myAvailability
    val states = listOf(memberState, summaryState, myState)

    if (states.any { state -> state is CalendarRemoteLoadState.Failure }) {
        return EventAvailabilityDisplayModel(
            loadState = EventSectionLoadState.Failed,
            isMutating = isAvailabilityMutationLoading,
            mutationError = availabilityMutationError,
        )
    }

    if (memberState !is CalendarRemoteLoadState.Success ||
        summaryState !is CalendarRemoteLoadState.Success ||
        myState !is CalendarRemoteLoadState.Success
    ) {
        return EventAvailabilityDisplayModel(
            isMutating = isAvailabilityMutationLoading,
            mutationError = availabilityMutationError,
        )
    }

    val members = memberState.value?.members.orEmpty()
    val summary = summaryState.value
    val participantCount = summary?.participantCount ?: members.size
    val cells = members.toAvailabilityCells(participantCount)
    val everyoneRanges = summary?.timeSlots.orEmpty()
        .filter { timeSlot -> timeSlot.isAvailableForEveryone }
        .mapNotNull { timeSlot ->
            val rangeStart = timeSlot.startTime.toAvailabilityTimeOrNull() ?: return@mapNotNull null
            val rangeEnd = timeSlot.endTime.toAvailabilityTimeOrNull() ?: return@mapNotNull null

            EventAvailabilityRangeUiModel(
                startTime = rangeStart,
                endTime = rangeEnd,
                availableCount = timeSlot.availableCount,
            )
        }
    val myTimeSlots = myState.value?.timeSlots.orEmpty()

    return EventAvailabilityDisplayModel(
        loadState = EventSectionLoadState.Loaded,
        cells = cells,
        participantCount = participantCount,
        respondedCount = summary?.respondedCount ?: members.count { member -> member.timeSlots.isNotEmpty() },
        everyoneRanges = everyoneRanges,
        bestRange = cells.toBestRange(),
        mySlotStarts = myTimeSlots.toSlotStarts(),
        hasMyAvailability = myTimeSlots.isNotEmpty(),
        isMutating = isAvailabilityMutationLoading,
        mutationError = availabilityMutationError,
    )
}
