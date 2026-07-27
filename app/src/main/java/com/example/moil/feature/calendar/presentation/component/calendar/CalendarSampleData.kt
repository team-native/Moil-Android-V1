package com.example.moil.feature.calendar.presentation.component

import androidx.annotation.StringRes
import com.example.moil.R
import java.time.DayOfWeek
import java.time.LocalDate

internal val calendarWeekdayNameResources = listOf(
    R.string.calendar_weekday_sunday,
    R.string.calendar_weekday_monday,
    R.string.calendar_weekday_tuesday,
    R.string.calendar_weekday_wednesday,
    R.string.calendar_weekday_thursday,
    R.string.calendar_weekday_friday,
    R.string.calendar_weekday_saturday,
)

internal val calendarGroups = listOf(
    CalendarGroup(R.string.calendar_family_name),
    CalendarGroup(R.string.calendar_group_college),
    CalendarGroup(R.string.calendar_group_work),
)

internal val calendarFamilyMemberAvatarResources = listOf(
    R.drawable.family_avatar_mine,
    R.drawable.family_avatar_mom,
    R.drawable.family_avatar_dad,
    R.drawable.family_avatar_sibling,
)

internal fun DayOfWeek.sundayFirstOffset(): Int = value % 7

internal fun calendarEventsFor(date: LocalDate): List<CalendarEvent> = when (date) {
    LocalDate.of(2026, 7, 5) -> listOf(CalendarEvent(R.string.calendar_event_family_meal, CalendarEventColor.Yellow))
    LocalDate.of(2026, 7, 9) -> listOf(CalendarEvent(R.string.calendar_event_trip, CalendarEventColor.Blue))
    LocalDate.of(2026, 7, 16) -> listOf(CalendarEvent(R.string.calendar_event_hospital, CalendarEventColor.Green))
    LocalDate.of(2026, 7, 22) -> listOf(
        CalendarEvent(R.string.calendar_event_trip, CalendarEventColor.Blue),
        CalendarEvent(R.string.calendar_event_day_off, CalendarEventColor.Green),
    )
    LocalDate.of(2026, 7, 28) -> listOf(CalendarEvent(R.string.calendar_event_birthday, CalendarEventColor.Yellow))
    else -> emptyList()
}

internal data class CalendarGroup(
    @param:StringRes val nameRes: Int,
)

internal data class CalendarEvent(
    @param:StringRes val titleRes: Int,
    val color: CalendarEventColor,
)

internal enum class CalendarEventColor { Blue, Green, Yellow }
