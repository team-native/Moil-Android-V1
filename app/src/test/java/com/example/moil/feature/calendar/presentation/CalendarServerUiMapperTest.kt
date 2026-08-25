package com.example.moil.feature.calendar.presentation

import com.example.moil.feature.event.module.domain.model.EventMember
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.group.domain.GroupColor
import org.junit.Assert.assertEquals
import org.junit.Test

class CalendarServerUiMapperTest {

    @Test
    fun `첫 참여자 프로필 색을 캘린더 일정 박스 색으로 사용한다`() {
        val eventUiModel = listOf(
            event(
                members = listOf(EventMember(1L, "초록", GroupColor.Green)),
            ),
        ).toCalendarEvents(GroupColor.Red).single()

        assertEquals(GroupColor.Green, eventUiModel.displayColor)
    }

    @Test
    fun `참여자가 없으면 선택 그룹 내 프로필 색을 사용한다`() {
        val eventUiModel = listOf(event()).toCalendarEvents(GroupColor.Green).single()

        assertEquals(GroupColor.Green, eventUiModel.displayColor)
    }

    @Test
    fun `겹치는 기간 일정은 서로 다른 라인을 배정한다`() {
        val calendarEvents = listOf(
            event(id = 1L, startDate = "2026-08-03", endDate = "2026-08-05"),
            event(id = 2L, startDate = "2026-08-04", endDate = "2026-08-06"),
            event(id = 3L, startDate = "2026-08-07", endDate = "2026-08-08"),
        ).toCalendarEvents(GroupColor.Green)

        assertEquals(listOf(0, 1, 0), calendarEvents.map { event -> event.lineIndex })
    }

    @Test
    fun `종료일이 시작일보다 빠른 일정은 표시 대상에서 제외한다`() {
        val calendarEvents = listOf(
            event(startDate = "2026-08-05", endDate = "2026-08-03"),
        ).toCalendarEvents(GroupColor.Green)

        assertEquals(emptyList<CalendarEventUiModel>(), calendarEvents)
    }

    private fun event(
        id: Long = 1L,
        startDate: String = "2026-08-03",
        endDate: String = "2026-08-03",
        members: List<EventMember> = emptyList(),
    ): GroupEvent = GroupEvent(
        id = id,
        title = "가족 일정",
        startDate = startDate,
        endDate = endDate,
        isAllDay = true,
        startTime = null,
        endTime = null,
        location = null,
        members = members,
    )
}
