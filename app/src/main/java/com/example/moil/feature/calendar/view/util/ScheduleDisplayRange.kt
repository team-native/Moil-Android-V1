package com.example.moil.feature.calendar.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import java.time.LocalTime

@Composable
internal fun LocalTime.toScheduleDisplayTime(): String = toKoreanTimeDisplay()

@Composable
internal fun formatScheduleTimeRange(
    startTime: LocalTime?,
    endTime: LocalTime?,
): String = when {
    startTime == null && endTime == null -> stringResource(R.string.schedule_all_day)
    startTime != null && endTime != null -> stringResource(
        R.string.schedule_time_range,
        startTime.toKoreanTimeDisplay(),
        endTime.toKoreanTimeDisplay(),
    )
    else -> stringResource(R.string.schedule_time_unknown)
}

@Composable
internal fun LocalTime.toShortScheduleTime(): String = stringResource(
    R.string.schedule_time_short,
    hour,
    minute,
)
