package com.example.moil.feature.calendar.presentation

import java.time.LocalDate
import java.time.YearMonth
import com.example.moil.R
import com.example.moil.feature.group.module.domain.model.GroupColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarUiStateReducerTest {
    private val initialUiState = CalendarUiState(
        displayedMonth = YearMonth.of(2026, 7),
        selectedDate = LocalDate.of(2026, 7, 22),
    )

    @Test
    fun dateClicked_selectsDateClosesGroupMenuAndOpensScheduleSheet() {
        val selectedDate = LocalDate.of(2026, 7, 25)

        val updatedUiState = initialUiState.copy(isGroupMenuVisible = true)
            .reduce(CalendarScreenEvent.DateClicked(selectedDate))

        assertEquals(selectedDate, updatedUiState.selectedDate)
        assertFalse(updatedUiState.isGroupMenuVisible)
        assertTrue(updatedUiState.isScheduleSheetVisible)
    }

    @Test
    fun dateClicked_기본으로_현재_사용자를_공유_구성원으로_선택한다() {
        val currentMemberId = 7L
        val updatedUiState = initialUiState.copy(
            members = listOf(
                CalendarMemberUiModel(
                    id = currentMemberId,
                    name = "나",
                    color = GroupColor.Green,
                    isCurrentUser = true,
                    avatarRes = R.drawable.family_avatar_member_green,
                ),
            ),
        ).reduce(CalendarScreenEvent.DateClicked(LocalDate.of(2026, 7, 25)))

        assertEquals(setOf(currentMemberId), updatedUiState.sharedMemberIds)
    }

    @Test
    fun scheduleSheetDismissed_closesScheduleSheet() {
        val updatedUiState = initialUiState.copy(isScheduleSheetVisible = true)
            .reduce(CalendarScreenEvent.ScheduleSheetDismissed)

        assertFalse(updatedUiState.isScheduleSheetVisible)
    }

    @Test
    fun scheduleStartDateChanged_종료일이_이전이면_시작일로_보정한다() {
        val updatedUiState = initialUiState.copy(
            scheduleStartDate = LocalDate.of(2026, 7, 22),
            scheduleEndDate = LocalDate.of(2026, 7, 23),
        ).reduce(CalendarScreenEvent.ScheduleStartDateChanged(LocalDate.of(2026, 7, 25)))

        assertEquals(LocalDate.of(2026, 7, 25), updatedUiState.scheduleStartDate)
        assertEquals(LocalDate.of(2026, 7, 25), updatedUiState.scheduleEndDate)
    }

    @Test
    fun scheduleEndDateChanged_시작일보다_이전이면_시작일로_보정한다() {
        val updatedUiState = initialUiState.copy(
            scheduleStartDate = LocalDate.of(2026, 7, 22),
        ).reduce(CalendarScreenEvent.ScheduleEndDateChanged(LocalDate.of(2026, 7, 20)))

        assertEquals(LocalDate.of(2026, 7, 22), updatedUiState.scheduleEndDate)
    }

    @Test
    fun sharedMemberClicked_togglesMemberSelection() {
        val selectedUiState = initialUiState.reduce(
            CalendarScreenEvent.SharedMemberClicked(2L),
        )
        val deselectedUiState = selectedUiState.reduce(
            CalendarScreenEvent.SharedMemberClicked(2L),
        )

        assertTrue(2L in selectedUiState.sharedMemberIds)
        assertFalse(2L in deselectedUiState.sharedMemberIds)
    }
}
