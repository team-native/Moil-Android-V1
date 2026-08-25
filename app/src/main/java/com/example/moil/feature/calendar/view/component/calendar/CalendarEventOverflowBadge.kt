package com.example.moil.feature.calendar.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moil.R

@Composable
internal fun CalendarEventOverflowBadge(hiddenEventCount: Int) {
    val eventCountDescription = stringResource(
        R.string.calendar_more_events_content_description,
        hiddenEventCount,
    )

    Text(
        text = stringResource(R.string.calendar_more_events, hiddenEventCount),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
            .semantics {
                contentDescription = eventCountDescription
            },
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.Center,
    )
}
