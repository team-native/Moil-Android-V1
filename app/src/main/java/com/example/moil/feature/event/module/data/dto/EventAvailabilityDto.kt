package com.example.moil.feature.event.module.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateEventAvailabilityRequestDto(
    @SerialName("date")
    val date: String,
    @SerialName("timeSlots")
    val timeSlots: List<EventAvailabilityTimeSlotDto>,
)

@Serializable
data class EventAvailabilityTimeSlotDto(
    @SerialName("startTime")
    val startTime: String,
    @SerialName("endTime")
    val endTime: String,
)

@Serializable
data class MyEventAvailabilityResponseDto(
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("date")
    val date: String,
    @SerialName("userId")
    val userId: Long,
    @SerialName("timeSlots")
    val timeSlots: List<EventAvailabilityTimeSlotDto>,
)

@Serializable
data class EventAvailabilityResponseDto(
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("date")
    val date: String,
    @SerialName("members")
    val members: List<EventMemberAvailabilityResponseDto>,
)

@Serializable
data class EventMemberAvailabilityResponseDto(
    @SerialName("userId")
    val userId: Long,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("colorId")
    val colorId: String? = null,
    @SerialName("timeSlots")
    val timeSlots: List<EventAvailabilityTimeSlotDto>,
)

@Serializable
data class EventAvailabilitySummaryResponseDto(
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("date")
    val date: String,
    @SerialName("participantCount")
    val participantCount: Int,
    @SerialName("respondedCount")
    val respondedCount: Int,
    @SerialName("timeSlots")
    val timeSlots: List<EventAvailabilityCountResponseDto>,
)

@Serializable
data class EventAvailabilityCountResponseDto(
    @SerialName("startTime")
    val startTime: String,
    @SerialName("endTime")
    val endTime: String,
    @SerialName("availableCount")
    val availableCount: Int,
    @SerialName("availableMemberIds")
    val availableMemberIds: List<Long>,
    @SerialName("isAvailableForEveryone")
    val isAvailableForEveryone: Boolean,
)
