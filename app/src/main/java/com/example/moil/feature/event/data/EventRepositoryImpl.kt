package com.example.moil.feature.event.data

import com.example.moil.core.domain.MoilResult
import com.example.moil.core.domain.mapToDomain
import com.example.moil.feature.event.data.remote.EventMemberResponseDto
import com.example.moil.feature.event.data.remote.EventRemoteDataSource
import com.example.moil.feature.event.data.remote.EventRequestDto
import com.example.moil.feature.event.data.remote.EventResponseDto
import com.example.moil.feature.event.data.remote.UpdateEventRequestDto
import com.example.moil.feature.event.domain.EventMember
import com.example.moil.feature.event.domain.EventRepository
import com.example.moil.feature.event.domain.GroupEvent
import com.example.moil.feature.group.domain.toGroupColor
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventRemoteDataSource: EventRemoteDataSource,
) : EventRepository {
    override suspend fun getGroupEvents(groupId: Long, month: String): MoilResult<List<GroupEvent>> = eventRemoteDataSource
        .getGroupEvents(groupId, month)
        .mapToDomain { events -> events.map { it.toDomain() } }

    override suspend fun createEvent(event: GroupEvent, groupId: Long, memberIds: List<Long>): MoilResult<Long> = eventRemoteDataSource
        .createEvent(event.toCreateRequest(groupId, memberIds))
        .mapToDomain { it.eventId }

    override suspend fun getEvent(eventId: Long): MoilResult<GroupEvent> = eventRemoteDataSource
        .getEvent(eventId)
        .mapToDomain { it.toDomain() }

    override suspend fun updateEvent(eventId: Long, event: GroupEvent, memberIds: List<Long>): MoilResult<Unit> = eventRemoteDataSource
        .updateEvent(eventId, event.toUpdateRequest(memberIds))
        .mapToDomain { Unit }

    override suspend fun deleteEvent(eventId: Long): MoilResult<Unit> = eventRemoteDataSource
        .deleteEvent(eventId)
        .mapToDomain { Unit }
}

private fun EventResponseDto.toDomain(): GroupEvent = GroupEvent(
    id = eventId,
    title = title,
    date = date,
    isAllDay = isAllDay,
    startTime = startTime,
    endTime = endTime,
    location = location,
    members = members.map { it.toDomain() },
)

private fun EventMemberResponseDto.toDomain(): EventMember = EventMember(userId, nickname, colorId.toGroupColor())

private fun GroupEvent.toCreateRequest(groupId: Long, memberIds: List<Long>): EventRequestDto = EventRequestDto(groupId, title, date, isAllDay, startTime, endTime, location, memberIds)
private fun GroupEvent.toUpdateRequest(memberIds: List<Long>): UpdateEventRequestDto = UpdateEventRequestDto(title, date, isAllDay, startTime, endTime, location, memberIds)
