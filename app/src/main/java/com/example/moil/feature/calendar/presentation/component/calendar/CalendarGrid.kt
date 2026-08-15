package com.example.moil.feature.calendar.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.LocalDate
import java.time.YearMonth
import com.example.moil.feature.calendar.presentation.CalendarEventUiModel

@Composable
internal fun CalendarGrid(
    displayedMonth: YearMonth,
    eventsByDate: Map<LocalDate, List<CalendarEventUiModel>>,
    modifier: Modifier,
    onDateClick: (LocalDate) -> Unit,
) {
    val todayDate = LocalDate.now()

    Column(modifier = modifier.fillMaxWidth()) {
        CalendarWeekdayHeader()

        val firstDayOffset = displayedMonth.atDay(1).dayOfWeek.sundayFirstOffset()
        val visibleCellCount = firstDayOffset + displayedMonth.lengthOfMonth()
        val weekCount = if (visibleCellCount <= 35) 5 else 6
        val firstVisibleDate = displayedMonth
            .atDay(1)
            .minusDays(firstDayOffset.toLong())

        repeat(weekCount) { weekIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                repeat(7) { dayIndex ->
                    val date = firstVisibleDate.plusDays((weekIndex * 7 + dayIndex).toLong())
                    CalendarDayCell(
                        date = date,
                        isDisplayedMonth = date.month == displayedMonth.month,
                        isToday = date == todayDate,
                        events = eventsByDate[date].orEmpty(),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { onDateClick(date) },
                    )
                }
            }
        }
    }
}
