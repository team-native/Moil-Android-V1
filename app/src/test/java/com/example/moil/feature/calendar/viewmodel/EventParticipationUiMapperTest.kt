package com.example.moil.feature.calendar.viewmodel

import com.example.moil.feature.event.module.domain.model.EventAttendance
import com.example.moil.feature.event.module.domain.model.EventAttendanceStatus
import com.example.moil.feature.event.module.domain.model.EventAvailabilityTimeSlot
import com.example.moil.feature.event.module.domain.model.EventMemberAvailability
import com.example.moil.feature.group.module.domain.model.GroupColor
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EventParticipationUiMapperTest {
    @Test
    fun `이어지는 칸은 하나의 시간대로 합쳐 HH mm 형식으로 보낸다`() {
        val selectedSlotStarts = setOf(
            LocalTime.of(12, 0),
            LocalTime.of(12, 30),
            LocalTime.of(17, 0),
        )

        val timeSlots = selectedSlotStarts.toAvailabilityTimeSlots()

        assertEquals(
            listOf(
                EventAvailabilityTimeSlot(startTime = "12:00", endTime = "13:00"),
                EventAvailabilityTimeSlot(startTime = "17:00", endTime = "17:30"),
            ),
            timeSlots,
        )
    }

    @Test
    fun `분 단위 서버 시간대는 겹치는 30분 칸으로 넓혀 표시한다`() {
        val slotStarts = listOf(
            EventAvailabilityTimeSlot(startTime = "12:31", endTime = "13:42"),
        ).toSlotStarts()

        assertEquals(
            setOf(
                LocalTime.of(12, 30),
                LocalTime.of(13, 0),
                LocalTime.of(13, 30),
            ),
            slotStarts,
        )
    }

    @Test
    fun `읽을 수 없는 서버 시각은 무시하고 앱이 종료되지 않는다`() {
        val slotStarts = listOf(
            EventAvailabilityTimeSlot(startTime = "noon", endTime = "13:00"),
        ).toSlotStarts()

        assertTrue(slotStarts.isEmpty())
    }

    @Test
    fun `칸별 가능 인원을 세고 참여자 모두 가능한 칸을 표시한다`() {
        val members = listOf(
            memberAvailability(userId = 1L, "12:00" to "13:00"),
            memberAvailability(userId = 2L, "12:30" to "13:00"),
        )

        val cells = members.toAvailabilityCells(participantCount = 2)
        val cellAtNoon = cells.first { cell -> cell.startTime == LocalTime.of(12, 0) }
        val cellAtHalfPast = cells.first { cell -> cell.startTime == LocalTime.of(12, 30) }

        assertEquals(1, cellAtNoon.availableCount)
        assertFalse(cellAtNoon.isAvailableForEveryone)
        assertEquals(2, cellAtHalfPast.availableCount)
        assertTrue(cellAtHalfPast.isAvailableForEveryone)
        assertEquals(
            EventAvailabilityRangeUiModel(LocalTime.of(12, 30), LocalTime.of(13, 0), 2),
            cells.toBestRange(),
        )
    }

    @Test
    fun `아무도 가능하지 않으면 가장 많이 되는 시간이 없다`() {
        val cells = emptyList<EventMemberAvailability>().toAvailabilityCells(participantCount = 3)

        assertNull(cells.toBestRange())
    }

    @Test
    fun `다른 일정의 참석 현황은 선택한 일정 섹션에 보여주지 않는다`() {
        val remoteState = CalendarRemoteUiState(
            attendanceEventId = 1L,
            attendance = CalendarRemoteLoadState.Success(
                EventAttendance(
                    myStatus = EventAttendanceStatus.Attending,
                    participantCount = 4,
                    attendingCount = 2,
                    declinedCount = 1,
                    members = emptyList(),
                ),
            ),
        )

        assertEquals(EventSectionLoadState.Loading, remoteState.toEventAttendanceUiModel(eventId = 2L).loadState)

        val attendanceUiModel = remoteState.toEventAttendanceUiModel(eventId = 1L)

        assertEquals(EventSectionLoadState.Loaded, attendanceUiModel.loadState)
        assertEquals(1, attendanceUiModel.pendingCount)
        assertEquals(EventAttendanceStatus.Attending, attendanceUiModel.myStatus)
    }

    private fun memberAvailability(
        userId: Long,
        vararg ranges: Pair<String, String>,
    ): EventMemberAvailability = EventMemberAvailability(
        userId = userId,
        nickname = "멤버$userId",
        color = GroupColor.Sky,
        timeSlots = ranges.map { (startTime, endTime) ->
            EventAvailabilityTimeSlot(startTime = startTime, endTime = endTime)
        },
    )
}
