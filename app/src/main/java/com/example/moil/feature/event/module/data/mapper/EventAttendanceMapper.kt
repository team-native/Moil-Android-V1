package com.example.moil.feature.event.module.data.mapper

import com.example.moil.feature.event.module.data.dto.EventAttendanceMemberResponseDto
import com.example.moil.feature.event.module.data.dto.EventAttendanceRequestStatus
import com.example.moil.feature.event.module.data.dto.EventAttendanceResponseDto
import com.example.moil.feature.event.module.data.dto.EventAttendanceUpdateResponseDto
import com.example.moil.feature.event.module.data.dto.UpdateEventAttendanceRequestDto
import com.example.moil.feature.event.module.domain.model.EventAttendance
import com.example.moil.feature.event.module.domain.model.EventAttendanceChoice
import com.example.moil.feature.event.module.domain.model.EventAttendanceMember
import com.example.moil.feature.event.module.domain.model.EventAttendanceStatus
import com.example.moil.feature.event.module.domain.model.EventAttendanceUpdate
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.toGroupColor

internal fun EventAttendanceResponseDto.toEventAttendanceDomain(): EventAttendance = EventAttendance(
    myStatus = myStatus.toDomainStatus(),
    participantCount = participantCount,
    attendingCount = attendingCount,
    declinedCount = declinedCount,
    members = members.map(EventAttendanceMemberResponseDto::toEventAttendanceMemberDomain),
)

internal fun EventAttendanceMemberResponseDto.toEventAttendanceMemberDomain(): EventAttendanceMember = EventAttendanceMember(
    memberId = memberId,
    nickname = nickname,
    color = colorId?.toGroupColor() ?: GroupColor.Unknown,
    status = status.toDomainStatus(),
)

internal fun EventAttendanceUpdateResponseDto.toEventAttendanceUpdateDomain(): EventAttendanceUpdate = EventAttendanceUpdate(
    status = status.toDomainStatus() ?: EventAttendanceStatus.Unknown,
    updatedAt = updatedAt,
)

internal fun EventAttendanceChoice.toRequest(): UpdateEventAttendanceRequestDto =
    UpdateEventAttendanceRequestDto(
        status = when (this) {
            EventAttendanceChoice.Attending -> EventAttendanceRequestStatus.Attending
            EventAttendanceChoice.Declined -> EventAttendanceRequestStatus.Declined
        },
    )

private fun String?.toDomainStatus(): EventAttendanceStatus? = when (this) {
    null -> null
    "ATTENDING" -> EventAttendanceStatus.Attending
    "DECLINED" -> EventAttendanceStatus.Declined
    else -> EventAttendanceStatus.Unknown
}
