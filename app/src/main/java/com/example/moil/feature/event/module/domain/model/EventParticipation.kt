package com.example.moil.feature.event.module.domain.model

import com.example.moil.feature.group.module.domain.model.GroupColor

enum class EventAttendanceChoice {
    Attending,
    Declined,
}

enum class EventAttendanceStatus {
    Attending,
    Declined,
    Unknown,
}

data class EventAttendance(
    val myStatus: EventAttendanceStatus?,
    val participantCount: Int,
    val attendingCount: Int,
    val declinedCount: Int,
    val members: List<EventAttendanceMember>,
)

data class EventAttendanceMember(
    val memberId: Long,
    val nickname: String,
    val color: GroupColor,
    val status: EventAttendanceStatus?,
)

data class EventAttendanceUpdate(
    val status: EventAttendanceStatus,
    val updatedAt: String,
)
