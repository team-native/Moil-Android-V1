package com.example.moil.feature.event.module.domain.model

import com.example.moil.feature.group.module.domain.model.GroupColor

data class EventAvailabilityTimeSlot(
    val startTime: String,
    val endTime: String,
)

data class MyEventAvailability(
    val eventId: Long,
    val date: String,
    val userId: Long,
    val timeSlots: List<EventAvailabilityTimeSlot>,
)

data class EventAvailability(
    val eventId: Long,
    val date: String,
    val members: List<EventMemberAvailability>,
)

data class EventMemberAvailability(
    val userId: Long,
    val nickname: String,
    val color: GroupColor?,
    val timeSlots: List<EventAvailabilityTimeSlot>,
)

data class EventAvailabilitySummary(
    val eventId: Long,
    val date: String,
    val participantCount: Int,
    val respondedCount: Int,
    val timeSlots: List<EventAvailabilityCount>,
)

data class EventAvailabilityCount(
    val startTime: String,
    val endTime: String,
    val availableCount: Int,
    val availableMemberIds: List<Long>,
    val isAvailableForEveryone: Boolean,
)
