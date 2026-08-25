package com.example.moil.feature.calendar.view

import com.example.moil.R
import java.time.DayOfWeek

internal val calendarWeekdayNameResources = listOf(
    R.string.calendar_weekday_sunday,
    R.string.calendar_weekday_monday,
    R.string.calendar_weekday_tuesday,
    R.string.calendar_weekday_wednesday,
    R.string.calendar_weekday_thursday,
    R.string.calendar_weekday_friday,
    R.string.calendar_weekday_saturday,
)

internal fun DayOfWeek.sundayFirstOffset(): Int = value % 7
