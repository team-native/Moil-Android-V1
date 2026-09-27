package com.example.moil.feature.event.module.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateEventAttendanceRequestDto(
    @SerialName("status")
    val status: EventAttendanceRequestStatus,
)

@Serializable
enum class EventAttendanceRequestStatus {
    @SerialName("ATTENDING")
    Attending,

    @SerialName("DECLINED")
    Declined,
}

@Serializable
data class EventAttendanceResponseDto(
    @SerialName("myStatus")
    val myStatus: String? = null,
    @SerialName("participantCount")
    val participantCount: Int,
    @SerialName("attendingCount")
    val attendingCount: Int,
    @SerialName("declinedCount")
    val declinedCount: Int,
    @SerialName("members")
    val members: List<EventAttendanceMemberResponseDto>,
)

@Serializable
data class EventAttendanceMemberResponseDto(
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("colorId")
    val colorId: String? = null,
    @SerialName("status")
    val status: String? = null,
)

@Serializable
data class EventAttendanceUpdateResponseDto(
    @SerialName("status")
    val status: String,
    @SerialName("updatedAt")
    val updatedAt: String,
)
