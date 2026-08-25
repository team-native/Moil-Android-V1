package com.example.moil.feature.calendar.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.example.moil.feature.calendar.viewmodel.CalendarEventUiModel
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilCalendarDimension
import com.example.moil.ui.theme.MoilRadius
import androidx.compose.ui.unit.dp

@Composable
internal fun CalendarEventBadge(
    calendarEvent: CalendarEventUiModel,
    modifier: Modifier = Modifier,
    isRangeStart: Boolean = true,
    isRangeEnd: Boolean = true,
) {
    val extraColors = LocalMoilExtraColors.current
    val eventShape = RoundedCornerShape(
        topStart = if (isRangeStart) MoilRadius.Event else 0.dp,
        bottomStart = if (isRangeStart) MoilRadius.Event else 0.dp,
        topEnd = if (isRangeEnd) MoilRadius.Event else 0.dp,
        bottomEnd = if (isRangeEnd) MoilRadius.Event else 0.dp,
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MoilCalendarDimension.EventBadgeOuterHorizontalPadding)
            .clip(eventShape)
            .background(extraColors.colorForProfile(calendarEvent.displayColor))
            .padding(
                horizontal = MoilCalendarDimension.EventBadgeHorizontalPadding,
                vertical = MoilCalendarDimension.EventBadgeVerticalPadding,
            )
            .semantics { contentDescription = calendarEvent.title },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = calendarEvent.title,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

internal fun com.example.moil.ui.theme.MoilExtraColors.colorForProfile(
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
