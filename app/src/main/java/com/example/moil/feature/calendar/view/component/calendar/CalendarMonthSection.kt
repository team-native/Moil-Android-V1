package com.example.moil.feature.calendar.view

import androidx.compose.runtime.Composable
import java.time.YearMonth

@Composable
internal fun CalendarMonthSection(
    displayedMonth: YearMonth,
    isGroupMenuVisible: Boolean,
    isScheduleSheetVisible: Boolean,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
) {
    CalendarMonthHeader(
        displayedMonth = displayedMonth,
        isCalendarContentDimmed = isGroupMenuVisible || isScheduleSheetVisible,
        showNavigation = !isGroupMenuVisible,
        onPreviousMonthClick = onPreviousMonthClick,
        onNextMonthClick = onNextMonthClick,
    )
}
