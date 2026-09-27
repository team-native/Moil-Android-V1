package com.example.moil.feature.event.module.data.mapper

import com.example.moil.feature.event.module.data.dto.EventAvailabilityCountResponseDto
import com.example.moil.feature.event.module.data.dto.EventAvailabilityResponseDto
import com.example.moil.feature.event.module.data.dto.EventAvailabilitySummaryResponseDto
import com.example.moil.feature.event.module.data.dto.EventAvailabilityTimeSlotDto
import com.example.moil.feature.event.module.data.dto.EventMemberAvailabilityResponseDto
import com.example.moil.feature.event.module.data.dto.MyEventAvailabilityResponseDto
import com.example.moil.feature.event.module.data.dto.UpdateEventAvailabilityRequestDto
import com.example.moil.feature.event.module.domain.model.EventAvailability
import com.example.moil.feature.event.module.domain.model.EventAvailabilityCount
import com.example.moil.feature.event.module.domain.model.EventAvailabilitySummary
import com.example.moil.feature.event.module.domain.model.EventAvailabilityTimeSlot
import com.example.moil.feature.event.module.domain.model.EventMemberAvailability
import com.example.moil.feature.event.module.domain.model.MyEventAvailability
import com.example.moil.feature.group.module.domain.model.toGroupColor

internal fun MyEventAvailabilityResponseDto.toMyEventAvailabilityDomain(): MyEventAvailability = MyEventAvailability(
    eventId = eventId,
    date = date,
    userId = userId,
    timeSlots = timeSlots.map(EventAvailabilityTimeSlotDto::toEventAvailabilityTimeSlotDomain),
)

internal fun EventAvailabilityResponseDto.toEventAvailabilityDomain(): EventAvailability = EventAvailability(
    eventId = eventId,
    date = date,
    members = members.map(EventMemberAvailabilityResponseDto::toEventMemberAvailabilityDomain),
)

internal fun EventMemberAvailabilityResponseDto.toEventMemberAvailabilityDomain(): EventMemberAvailability = EventMemberAvailability(
    userId = userId,
    nickname = nickname,
    color = colorId?.toGroupColor(),
    timeSlots = timeSlots.map(EventAvailabilityTimeSlotDto::toEventAvailabilityTimeSlotDomain),
)

internal fun EventAvailabilitySummaryResponseDto.toEventAvailabilitySummaryDomain(): EventAvailabilitySummary =
    EventAvailabilitySummary(
        eventId = eventId,
        date = date,
        participantCount = participantCount,
        respondedCount = respondedCount,
        timeSlots = timeSlots.map(EventAvailabilityCountResponseDto::toEventAvailabilityCountDomain),
    )

internal fun EventAvailabilityCountResponseDto.toEventAvailabilityCountDomain(): EventAvailabilityCount = EventAvailabilityCount(
    startTime = startTime,
    endTime = endTime,
    availableCount = availableCount,
    availableMemberIds = availableMemberIds,
    isAvailableForEveryone = isAvailableForEveryone,
)

internal fun EventAvailabilityTimeSlotDto.toEventAvailabilityTimeSlotDomain(): EventAvailabilityTimeSlot = EventAvailabilityTimeSlot(
    startTime = startTime,
    endTime = endTime,
)

internal fun List<EventAvailabilityTimeSlot>.toRequest(
    date: String,
): UpdateEventAvailabilityRequestDto = UpdateEventAvailabilityRequestDto(
    date = date,
    timeSlots = map { slot ->
        EventAvailabilityTimeSlotDto(
            startTime = slot.startTime,
            endTime = slot.endTime,
        )
    },
)
