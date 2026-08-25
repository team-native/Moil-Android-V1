package com.example.moil.feature.event.module.data.mapper

import com.example.moil.feature.event.module.data.dto.EventMemberResponseDto
import com.example.moil.feature.event.module.data.dto.EventRequestDto
import com.example.moil.feature.event.module.data.dto.EventResponseDto
import com.example.moil.feature.event.module.data.dto.UpdateEventRequestDto
import com.example.moil.feature.event.module.domain.model.EventMember
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.group.domain.toGroupColor

internal fun EventResponseDto.toDomain(): GroupEvent = GroupEvent(
    id = eventId,
    title = title,
    startDate = startDate,
    endDate = endDate,
    isAllDay = isAllDay,
    startTime = startTime,
    endTime = endTime,
    location = location,
    members = members.map(EventMemberResponseDto::toDomain),
)

internal fun EventMemberResponseDto.toDomain(): EventMember = EventMember(
    userId = userId,
    nickname = nickname,
    color = colorId.toGroupColor(),
)

internal fun GroupEvent.toCreateRequest(
    groupId: Long,
    memberIds: List<Long>,
): EventRequestDto = EventRequestDto(
    groupId = groupId,
    title = title,
    startDate = startDate,
    endDate = endDate,
    isAllDay = isAllDay,
    startTime = startTime,
    endTime = endTime,
    location = location,
    sharedMemberIds = memberIds,
)

internal fun GroupEvent.toUpdateRequest(memberIds: List<Long>): UpdateEventRequestDto = UpdateEventRequestDto(
    title = title,
    startDate = startDate,
    endDate = endDate,
    isAllDay = isAllDay,
    startTime = startTime,
    endTime = endTime,
    location = location,
    sharedMemberIds = memberIds,
)
