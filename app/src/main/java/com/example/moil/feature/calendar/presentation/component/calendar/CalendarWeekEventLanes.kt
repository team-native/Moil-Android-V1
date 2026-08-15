package com.example.moil.feature.calendar.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.moil.feature.calendar.presentation.CalendarEventUiModel
import java.time.LocalDate

private const val MAX_VISIBLE_EVENT_LANES = 2
private const val DAYS_PER_WEEK = 7

@Composable
internal fun CalendarWeekEventLanes(
    events: List<CalendarEventUiModel>,
    weekStartDate: LocalDate,
) {
    val weekEndDate = weekStartDate.plusDays((DAYS_PER_WEEK - 1).toLong())
    val weekEvents = events.filter { event ->
        !event.endDate.isBefore(weekStartDate) && !event.startDate.isAfter(weekEndDate)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        (0 until MAX_VISIBLE_EVENT_LANES).forEach { lineIndex ->
            weekEvents.firstOrNull { event -> event.lineIndex == lineIndex }
                ?.let { event ->
                    CalendarWeekEventLane(
                        event = event,
                        weekStartDate = weekStartDate,
                        weekEndDate = weekEndDate,
                    )
                }
        }

        val hiddenEventCount = weekEvents.count { event ->
            event.lineIndex >= MAX_VISIBLE_EVENT_LANES
        }
        if (hiddenEventCount > 0) {
            CalendarEventOverflowBadge(hiddenEventCount)
        }
    }
}

@Composable
private fun CalendarWeekEventLane(
    event: CalendarEventUiModel,
    weekStartDate: LocalDate,
    weekEndDate: LocalDate,
) {
    val segmentStartDate = maxOf(event.startDate, weekStartDate)
    val segmentEndDate = minOf(event.endDate, weekEndDate)
    val leadingDayCount = java.time.temporal.ChronoUnit.DAYS
        .between(weekStartDate, segmentStartDate)
        .toInt()
    val spanDayCount = java.time.temporal.ChronoUnit.DAYS
        .between(segmentStartDate, segmentEndDate)
        .toInt() + 1
    val trailingDayCount = DAYS_PER_WEEK - leadingDayCount - spanDayCount

    Row(modifier = Modifier.fillMaxWidth()) {
        if (leadingDayCount > 0) {
            Spacer(modifier = Modifier.weight(leadingDayCount.toFloat()))
        }

        CalendarEventBadge(
            calendarEvent = event,
            modifier = Modifier.weight(spanDayCount.toFloat()),
            isRangeStart = segmentStartDate == event.startDate,
            isRangeEnd = segmentEndDate == event.endDate,
        )

        if (trailingDayCount > 0) {
            Spacer(modifier = Modifier.weight(trailingDayCount.toFloat()))
        }
    }
}
