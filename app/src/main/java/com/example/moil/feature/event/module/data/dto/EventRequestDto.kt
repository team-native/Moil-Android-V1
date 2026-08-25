package com.example.moil.feature.event.module.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventRequestDto(
    @SerialName("groupId") val groupId: Long,
    @SerialName("title") val title: String,
    @SerialName("startDate") val startDate: String,
    @SerialName("endDate") val endDate: String,
    @SerialName("isAllDay") val isAllDay: Boolean,
    @SerialName("startTime") val startTime: String? = null,
    @SerialName("endTime") val endTime: String? = null,
    @SerialName("location") val location: String? = null,
    @SerialName("sharedMemberIds") val sharedMemberIds: List<Long>,
)

@Serializable
data class UpdateEventRequestDto(
    @SerialName("title") val title: String,
    @SerialName("startDate") val startDate: String,
    @SerialName("endDate") val endDate: String,
    @SerialName("isAllDay") val isAllDay: Boolean,
    @SerialName("startTime") val startTime: String? = null,
    @SerialName("endTime") val endTime: String? = null,
    @SerialName("location") val location: String? = null,
    @SerialName("sharedMemberIds") val sharedMemberIds: List<Long>,
)
