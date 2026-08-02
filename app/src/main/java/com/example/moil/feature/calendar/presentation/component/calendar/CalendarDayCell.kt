package com.example.moil.feature.calendar.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.feature.calendar.presentation.CalendarEventUiModel
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilRadius
import java.time.LocalDate

@Composable
internal fun CalendarDayCell(
    date: LocalDate,
    isDisplayedMonth: Boolean,
    isSelected: Boolean,
    events: List<CalendarEventUiModel>,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val selectedDateDescription = stringResource(
        R.string.calendar_date_content_description,
        date.monthValue,
        date.dayOfMonth,
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(MoilRadius.Event))
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { contentDescription = selectedDateDescription },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    color = if (isSelected) {
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
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isDisplayedMonth -> MaterialTheme.colorScheme.onSurface
                    else -> LocalMoilExtraColors.current.calendarMutedText
                },
                style = MaterialTheme.typography.labelMedium,
            )
        }

        events.take(2).forEach { calendarEvent ->
            CalendarEventBadge(calendarEvent)
        }
    }
}
