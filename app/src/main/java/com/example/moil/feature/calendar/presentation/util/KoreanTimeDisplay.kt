package com.example.moil.feature.calendar.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import java.time.LocalTime

@Composable
internal fun LocalTime.toKoreanTimeDisplay(): String {
    val periodRes = if (hour < 12) {
        R.string.schedule_time_am
    } else {
        R.string.schedule_time_pm
    }
    val displayHour = when (val hourInTwelveHourFormat = hour % 12) {
        0 -> 12
        else -> hourInTwelveHourFormat
    }

    return stringResource(periodRes, displayHour, minute)
}
