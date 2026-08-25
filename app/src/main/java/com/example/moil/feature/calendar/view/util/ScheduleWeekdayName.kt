package com.example.moil.feature.calendar.view

import androidx.annotation.StringRes
import com.example.moil.R
import java.time.DayOfWeek
import java.time.LocalDate

@StringRes
internal fun weekdayNameRes(date: LocalDate): Int = when (date.dayOfWeek) {
    DayOfWeek.SUNDAY -> R.string.calendar_weekday_sunday
    DayOfWeek.MONDAY -> R.string.calendar_weekday_monday
    DayOfWeek.TUESDAY -> R.string.calendar_weekday_tuesday
    DayOfWeek.WEDNESDAY -> R.string.calendar_weekday_wednesday
    DayOfWeek.THURSDAY -> R.string.calendar_weekday_thursday
    DayOfWeek.FRIDAY -> R.string.calendar_weekday_friday
    DayOfWeek.SATURDAY -> R.string.calendar_weekday_saturday
}
