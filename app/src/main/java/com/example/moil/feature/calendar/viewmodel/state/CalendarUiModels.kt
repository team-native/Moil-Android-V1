package com.example.moil.feature.calendar.viewmodel

import androidx.annotation.DrawableRes
import com.example.moil.feature.group.module.domain.model.GroupColor
import java.time.LocalDate
import java.time.LocalTime

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
    val imagePath: String? = null,
)

data class CalendarEventUiModel(
    val id: Long,
    val title: String,
    val startDate: java.time.LocalDate,
    val endDate: java.time.LocalDate,
    val displayColor: GroupColor,
    val lineIndex: Int,
)

data class CalendarScheduleMemberUiModel(
    val id: Long,
    val name: String,
    val color: GroupColor,
    @param:DrawableRes val avatarRes: Int,
    val imagePath: String? = null,
)

data class CalendarScheduleUiModel(
    val id: Long,
    val title: String,
    val date: LocalDate,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val location: String?,
    val memo: String?,
    val members: List<CalendarScheduleMemberUiModel>,
    val displayColor: GroupColor,
)
