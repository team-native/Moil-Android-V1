package com.example.moil.feature.calendar.viewmodel

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
        assertEquals(CalendarScheduleSheetMode.List, updatedUiState.scheduleSheetMode)
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
    fun scheduleCreateClicked_switchesToCreateMode() {
        val updatedUiState = initialUiState.copy(isScheduleSheetVisible = true)
            .reduce(CalendarScreenEvent.ScheduleCreateClicked)

        assertEquals(CalendarScheduleSheetMode.Create, updatedUiState.scheduleSheetMode)
    }

    @Test
    fun scheduleItemClicked_switchesToDetailMode() {
        val updatedUiState = initialUiState.reduce(
            CalendarScreenEvent.ScheduleItemClicked(eventId = 12L),
        )

        assertEquals(CalendarScheduleSheetMode.Detail, updatedUiState.scheduleSheetMode)
        assertEquals(12L, updatedUiState.selectedEventId)
    }

    @Test
    fun scheduleEditCanceled_returnsToDetailMode() {
        val updatedUiState = initialUiState.copy(
            isScheduleSheetVisible = true,
            scheduleSheetMode = CalendarScheduleSheetMode.Edit,
            selectedEventId = 12L,
        ).reduce(CalendarScreenEvent.ScheduleEditCanceled)

        assertEquals(CalendarScheduleSheetMode.Detail, updatedUiState.scheduleSheetMode)
        assertTrue(updatedUiState.isScheduleSheetVisible)
    }

    @Test
    fun scheduleDateChanged_선택한_날짜를_수정한다() {
        val selectedDate = LocalDate.of(2026, 7, 25)

        val updatedUiState = initialUiState.reduce(
            CalendarScreenEvent.ScheduleDateChanged(selectedDate),
        )

        assertEquals(selectedDate, updatedUiState.scheduleDate)
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
