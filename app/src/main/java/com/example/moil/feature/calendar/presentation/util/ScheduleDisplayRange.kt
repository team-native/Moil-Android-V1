package com.example.moil.feature.calendar.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import java.time.LocalTime

@Composable
internal fun LocalTime.toScheduleDisplayRange(): String {
    val endTime = plusHours(1)

    return stringResource(
        R.string.schedule_time_range,
        toKoreanTimeDisplay(),
        endTime.toKoreanTimeDisplay(),
    )
}
