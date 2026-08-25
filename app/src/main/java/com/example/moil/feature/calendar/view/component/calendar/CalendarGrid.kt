package com.example.moil.feature.calendar.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.LocalDate
import java.time.YearMonth
import com.example.moil.feature.calendar.viewmodel.CalendarEventUiModel

@Composable
internal fun CalendarGrid(
    displayedMonth: YearMonth,
    events: List<CalendarEventUiModel>,
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
            val weekStartDate = firstVisibleDate.plusDays((weekIndex * 7).toLong())

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(7) { dayIndex ->
                        val date = weekStartDate.plusDays(dayIndex.toLong())
                        CalendarDayCell(
                            date = date,
                            isDisplayedMonth = date.month == displayedMonth.month,
                            isToday = date == todayDate,
                            eventCount = events.count { event ->
                                !date.isBefore(event.startDate) && !date.isAfter(event.endDate)
                            },
                            modifier = Modifier.weight(1f),
                            onClick = { onDateClick(date) },
                        )
                    }
                }

                CalendarWeekEventLanes(
                    events = events,
                    weekStartDate = weekStartDate,
                )
            }
        }
    }
}
