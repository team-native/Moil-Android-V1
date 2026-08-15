package com.example.moil.feature.calendar.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.feature.calendar.presentation.CalendarEventUiModel
import com.example.moil.feature.group.domain.GroupColor
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilRadius
import com.example.moil.ui.theme.MoilTheme
import java.time.LocalDate

@Composable
internal fun CalendarDayCell(
    date: LocalDate,
    isDisplayedMonth: Boolean,
    isToday: Boolean,
    events: List<CalendarEventUiModel>,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val dateDescription = stringResource(
        R.string.calendar_date_events_content_description,
        date.monthValue,
        date.dayOfMonth,
        events.size,
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(MoilRadius.Event))
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { contentDescription = dateDescription },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    color = if (isToday) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                color = when {
                    // Dates outside the displayed month always use the grey/95 token.
                    !isDisplayedMonth -> LocalMoilExtraColors.current.calendarMutedText
                    isToday -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.onSurface
                },
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        // Each visible schedule occupies its own date-local line.
        (0 until MAX_VISIBLE_EVENTS).forEach { lineIndex ->
            events.firstOrNull { calendarEvent -> calendarEvent.lineIndex == lineIndex }
                ?.let { calendarEvent ->
                    CalendarEventBadge(calendarEvent)
                }
        }

        val hiddenEventCount = events.count { calendarEvent ->
            calendarEvent.lineIndex >= MAX_VISIBLE_EVENTS
        }

        if (hiddenEventCount > 0) {
            CalendarEventOverflowBadge(hiddenEventCount)
        }
    }
}

private const val MAX_VISIBLE_EVENTS = 2

@Preview(showBackground = true, widthDp = 120)
@Composable
private fun CalendarDayCellSingleSchedulePreview() {
    MoilTheme(darkTheme = false) {
        CalendarDayCell(
            date = LocalDate.of(2026, 7, 9),
            isDisplayedMonth = true,
            isToday = false,
            events = listOf(
                CalendarEventUiModel(
                    title = "아빠 가족",
                    displayColor = GroupColor.Sky,
                    lineIndex = 0,
                ),
            ),
            modifier = Modifier.fillMaxWidth(),
            onClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 120)
@Composable
private fun CalendarDayCellMultipleSchedulesPreview() {
    MoilTheme(darkTheme = true) {
        CalendarDayCell(
            date = LocalDate.of(2026, 7, 22),
            isDisplayedMonth = true,
            isToday = true,
            events = listOf(
                CalendarEventUiModel(
                    title = "아빠 골프",
                    displayColor = GroupColor.Sky,
                    lineIndex = 0,
                ),
                CalendarEventUiModel(
                    title = "나 팀 회의",
                    displayColor = GroupColor.Green,
                    lineIndex = 1,
                ),
                CalendarEventUiModel(
                    title = "동생 시험",
                    displayColor = GroupColor.Yellow,
                    lineIndex = 2,
                ),
            ),
            modifier = Modifier.fillMaxWidth(),
            onClick = {},
        )
    }
}
