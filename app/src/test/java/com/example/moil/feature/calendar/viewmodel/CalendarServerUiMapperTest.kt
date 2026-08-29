package com.example.moil.feature.calendar.viewmodel

import com.example.moil.feature.event.module.domain.model.EventMember
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.GroupMember
import com.example.moil.feature.group.module.domain.model.GroupRole
import java.time.LocalDate
import java.time.LocalTime
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
    fun `일정 목록은 날짜와 시간 및 메모를 화면 모델로 변환한다`() {
        val scheduleUiModel = listOf(
            eventWithDetails(),
        ).toCalendarSchedules(GroupColor.Green).single()

        assertEquals(LocalDate.of(2026, 8, 3), scheduleUiModel.date)
        assertEquals(LocalTime.of(10, 0), scheduleUiModel.startTime)
        assertEquals(LocalTime.of(11, 30), scheduleUiModel.endTime)
        assertEquals("회의실 A", scheduleUiModel.location)
        assertEquals("신규 서비스 리뷰", scheduleUiModel.memo)
    }

    @Test
    fun `일정 참여자의 이미지는 그룹 멤버 정보로 보강한다`() {
        val scheduleUiModel = listOf(
            eventWithDetails().copy(
                members = listOf(EventMember(7L, "모일", GroupColor.Sky)),
            ),
        ).toCalendarSchedules(
            fallbackProfileColor = GroupColor.Green,
            groupMembers = listOf(
                GroupMember(
                    userId = 7L,
                    nickname = "모일",
                    email = "moil@example.com",
                    role = GroupRole.Member,
                    color = GroupColor.Sky,
                    isMe = true,
                    imagePath = "/image/profile",
                ),
            ),
        ).single()

        assertEquals("/image/profile", scheduleUiModel.members.single().imagePath)
    }

    @Test
    fun `같은 날짜의 일정은 서로 다른 라인을 배정한다`() {
        val calendarEvents = listOf(
            event(id = 1L, date = "2026-08-03"),
            event(id = 2L, date = "2026-08-03"),
            event(id = 3L, date = "2026-08-07"),
        ).toCalendarEvents(GroupColor.Green)

        assertEquals(listOf(0, 1, 0), calendarEvents.map { event -> event.lineIndex })
    }

    @Test
    fun `날짜 형식이 잘못된 일정은 표시 대상에서 제외한다`() {
        val calendarEvents = listOf(
            event(date = "잘못된 날짜"),
        ).toCalendarEvents(GroupColor.Green)

        assertEquals(emptyList<CalendarEventUiModel>(), calendarEvents)
    }

    private fun event(
        id: Long = 1L,
        date: String = "2026-08-03",
        members: List<EventMember> = emptyList(),
    ): GroupEvent = GroupEvent(
        id = id,
        title = "가족 일정",
        date = date,
        startTime = null,
        endTime = null,
        location = null,
        memo = null,
        members = members,
    )

    private fun eventWithDetails(): GroupEvent = GroupEvent(
        id = 4L,
        title = "디자인 회의",
        date = "2026-08-03",
        startTime = "10:00",
        endTime = "11:30",
        location = "회의실 A",
        memo = "신규 서비스 리뷰",
        members = emptyList(),
    )
}
