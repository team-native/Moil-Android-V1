package com.example.moil.feature.event.module.domain.model

import com.example.moil.feature.group.domain.GroupColor

data class GroupEvent(
    val id: Long,
    val title: String,
    val startDate: String,
    val endDate: String,
    val isAllDay: Boolean,
    val startTime: String?,
    val endTime: String?,
    val location: String?,
    val members: List<EventMember>,
)

data class EventMember(val userId: Long, val nickname: String, val color: GroupColor)
