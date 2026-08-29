package com.example.moil.feature.event.module.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventMemberResponseDto(
    @SerialName("userId") val userId: Long,
    @SerialName("nickname") val nickname: String,
    @SerialName("colorId") val colorId: String,
)

@Serializable
data class CreateEventResponseDto(
    @SerialName("eventId") val eventId: Long,
)

@Serializable
data class EventResponseDto(
    @SerialName("eventId") val eventId: Long,
    @SerialName("groupId") val groupId: Long? = null,
    @SerialName("title") val title: String,
    @SerialName("date") val date: String,
    @SerialName("startTime") val startTime: String? = null,
    @SerialName("endTime") val endTime: String? = null,
    @SerialName("location") val location: String? = null,
    @SerialName("memo") val memo: String? = null,
    @SerialName("members") val members: List<EventMemberResponseDto> = emptyList(),
)
