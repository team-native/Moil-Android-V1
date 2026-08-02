package com.example.moil.feature.calendar.presentation

import androidx.annotation.DrawableRes

data class CalendarGroupUiModel(
    val id: Long,
    val name: String,
)

data class CalendarMemberUiModel(
    val id: Long,
    val name: String,
    @param:DrawableRes val avatarRes: Int,
)

data class CalendarEventUiModel(
    val title: String,
    val color: CalendarEventUiColor,
)

enum class CalendarEventUiColor {
    Blue,
    Green,
    Yellow,
}
