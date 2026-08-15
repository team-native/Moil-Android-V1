package com.example.moil.feature.calendar.presentation

import com.example.moil.feature.event.domain.EventMember
import com.example.moil.feature.event.domain.GroupEvent
import com.example.moil.feature.group.domain.GroupColor
import org.junit.Assert.assertEquals
import org.junit.Test

class CalendarServerUiMapperTest {

    @Test
    fun `참여자 프로필 색을 중복 없이 캘린더 일정에 표시한다`() {
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

        assertEquals(
            listOf(GroupColor.Green, GroupColor.Violet),
            eventUiModel.participantColors,
        )
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

        assertEquals(listOf(GroupColor.Green), eventUiModel.participantColors)
    }
}
