package com.example.moil.feature.calendar.viewmodel

import androidx.annotation.DrawableRes
import com.example.moil.feature.group.module.domain.model.GroupColor

data class CalendarGroupUiModel(
    val id: Long,
    val name: String,
)

data class CalendarMemberUiModel(
    val id: Long,
    val name: String,
    val color: GroupColor,
    val isCurrentUser: Boolean,
    @param:DrawableRes val avatarRes: Int,
)

data class CalendarEventUiModel(
    val id: Long,
    val title: String,
    val startDate: java.time.LocalDate,
    val endDate: java.time.LocalDate,
    val displayColor: GroupColor,
    val lineIndex: Int,
)
