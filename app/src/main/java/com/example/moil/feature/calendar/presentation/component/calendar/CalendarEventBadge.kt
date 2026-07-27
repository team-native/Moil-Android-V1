package com.example.moil.feature.calendar.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moil.ui.theme.LocalMoilExtraColors

@Composable
internal fun CalendarEventBadge(calendarEvent: CalendarEvent) {
    val extraColors = LocalMoilExtraColors.current
    val eventColor = when (calendarEvent.color) {
        CalendarEventColor.Blue -> extraColors.calendarEventBlue
        CalendarEventColor.Green -> extraColors.calendarEventGreen
        CalendarEventColor.Yellow -> extraColors.calendarEventYellow
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(eventColor),
        )

        Spacer(modifier = Modifier.width(2.dp))

        Text(
            text = stringResource(calendarEvent.titleRes),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
