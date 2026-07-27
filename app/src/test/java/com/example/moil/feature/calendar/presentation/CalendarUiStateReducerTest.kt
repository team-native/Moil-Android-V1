package com.example.moil.feature.calendar.presentation

import java.time.LocalDate
import java.time.YearMonth
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
    fun scheduleSheetDismissed_closesScheduleSheet() {
        val updatedUiState = initialUiState.copy(isScheduleSheetVisible = true)
            .reduce(CalendarScreenEvent.ScheduleSheetDismissed)

        assertFalse(updatedUiState.isScheduleSheetVisible)
    }

    @Test
    fun sharedMemberClicked_togglesMemberSelection() {
        val selectedUiState = initialUiState.reduce(
            CalendarScreenEvent.SharedMemberClicked(CalendarMemberId.Mom),
        )
        val deselectedUiState = selectedUiState.reduce(
            CalendarScreenEvent.SharedMemberClicked(CalendarMemberId.Mom),
        )

        assertTrue(CalendarMemberId.Mom in selectedUiState.sharedMemberIds)
        assertFalse(CalendarMemberId.Mom in deselectedUiState.sharedMemberIds)
    }
}
