package com.example.moil.feature.calendar.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moil.R
import java.time.YearMonth

@Composable
internal fun CalendarMonthHeader(
    displayedMonth: YearMonth,
    isCalendarContentDimmed: Boolean,
    showNavigation: Boolean,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val labelAlpha = if (isCalendarContentDimmed) 0.3f else 1f

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = stringResource(R.string.calendar_month_format, displayedMonth.monthValue),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = labelAlpha),
                style = MaterialTheme.typography.displaySmall,
            )
            Text(
                text = stringResource(R.string.calendar_year_format, displayedMonth.year),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = labelAlpha),
                style = MaterialTheme.typography.labelSmall,
            )
        }

        if (showNavigation) {
            Spacer(modifier = Modifier.weight(1f))

            CalendarMonthNavigationButton(
                drawableRes = R.drawable.common_chevron_previous,
                contentDescription = stringResource(R.string.calendar_previous_month),
                onClick = onPreviousMonthClick,
            )

            Spacer(modifier = Modifier.width(6.dp))

            CalendarMonthNavigationButton(
                drawableRes = R.drawable.common_chevron_next,
                contentDescription = stringResource(R.string.calendar_next_month),
                onClick = onNextMonthClick,
            )
        }
    }
}
