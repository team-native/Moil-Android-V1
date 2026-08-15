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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.moil.feature.calendar.presentation.CalendarEventUiModel
import com.example.moil.feature.group.domain.GroupColor
import com.example.moil.ui.theme.LocalMoilExtraColors

@Composable
internal fun CalendarEventBadge(calendarEvent: CalendarEventUiModel) {
    val extraColors = LocalMoilExtraColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
            .semantics {
                contentDescription = calendarEvent.title
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        calendarEvent.participantColors.forEach { profileColor ->
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(extraColors.colorForProfile(profileColor)),
            )

            Spacer(modifier = Modifier.width(1.dp))
        }

        Spacer(modifier = Modifier.width(1.dp))

        Text(
            text = calendarEvent.title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

private fun com.example.moil.ui.theme.MoilExtraColors.colorForProfile(
    profileColor: GroupColor,
): Color = when (profileColor) {
    GroupColor.Sky -> profileSky
    GroupColor.Red -> profileRed
    GroupColor.Green -> profileGreen
    GroupColor.Yellow -> profileYellow
    GroupColor.Teal -> profileTeal
    GroupColor.Violet -> profileViolet
    GroupColor.Magenta -> profileMagenta
    GroupColor.Unknown -> calendarMutedText
}
