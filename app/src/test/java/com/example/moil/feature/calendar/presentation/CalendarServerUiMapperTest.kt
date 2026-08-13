package com.example.moil.feature.calendar.presentation

import com.example.moil.feature.event.domain.EventMember
import com.example.moil.feature.event.domain.GroupEvent
import com.example.moil.feature.group.domain.GroupColor
import org.junit.Assert.assertEquals
import org.junit.Test

class CalendarServerUiMapperTest {

    @Test
    fun `첫 참여자 프로필 색을 캘린더 일정 박스 색으로 사용한다`() {
        val event = GroupEvent(
            id = 1L,
            title = "가족 모임",
            date = "2026-08-03",
            isAllDay = true,
            startTime = null,
            endTime = null,
            location = null,
            members = listOf(
                EventMember(1L, "초록", GroupColor.Green),
                EventMember(2L, "보라", GroupColor.Violet),
                EventMember(3L, "초록", GroupColor.Green),
            ),
        )

        val eventUiModel = listOf(event)
            .toCalendarEventsByDate(GroupColor.Red)
            .getValue(java.time.LocalDate.of(2026, 8, 3))
            .single()

        assertEquals(GroupColor.Green, eventUiModel.displayColor)
    }

    @Test
    fun `참여자가 없으면 선택 그룹 내 프로필 색을 사용한다`() {
        val event = GroupEvent(
            id = 1L,
            title = "개인 일정",
            date = "2026-08-03",
            isAllDay = true,
            startTime = null,
            endTime = null,
            location = null,
            members = emptyList(),
        )

        val eventUiModel = listOf(event)
            .toCalendarEventsByDate(GroupColor.Green)
            .getValue(java.time.LocalDate.of(2026, 8, 3))
            .single()

        assertEquals(GroupColor.Green, eventUiModel.displayColor)
    }

    @Test
    fun `같은 날짜 일정에 중복 없는 라인을 순서대로 배정한다`() {
        val events = listOf(
            event(id = 1L, title = "아침 일정"),
            event(id = 2L, title = "점심 일정"),
            event(id = 3L, title = "저녁 일정"),
        )

        val calendarEvents = events
            .toCalendarEventsByDate(GroupColor.Green)
            .getValue(java.time.LocalDate.of(2026, 8, 3))

        assertEquals(listOf(0, 1, 2), calendarEvents.map { event -> event.lineIndex })
    }

    private fun event(id: Long, title: String): GroupEvent = GroupEvent(
        id = id,
        title = title,
        date = "2026-08-03",
        isAllDay = true,
        startTime = null,
        endTime = null,
        location = null,
        members = emptyList(),
    )
}
