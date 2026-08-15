package com.example.moil.feature.event.data

import com.example.moil.core.domain.MoilResult
import com.example.moil.core.domain.mapToDomain
import com.example.moil.core.network.NetworkResult
import com.example.moil.feature.event.data.remote.EventMemberResponseDto
import com.example.moil.feature.event.data.remote.EventRemoteDataSource
import com.example.moil.feature.event.data.remote.EventRequestDto
import com.example.moil.feature.event.data.remote.EventResponseDto
import com.example.moil.feature.event.data.remote.UpdateEventRequestDto
import com.example.moil.feature.event.domain.EventMember
import com.example.moil.feature.event.domain.EventRepository
import com.example.moil.feature.event.domain.GroupEvent
import com.example.moil.feature.group.domain.toGroupColor
import java.time.YearMonth
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventRemoteDataSource: EventRemoteDataSource,
) : EventRepository {

    override suspend fun getGroupEvents(
        groupId: Long,
        month: YearMonth,
    ): MoilResult<List<GroupEvent>> = when (
        val result = eventRemoteDataSource.getGroupEvents(
            groupId = groupId,
            month = month.toString(),
        )
    ) {
        is NetworkResult.Success -> MoilResult.Success(result.data.map(EventResponseDto::toDomain))

        else -> result.mapToDomain { emptyList() }
    }

    override suspend fun createEvent(
        event: GroupEvent,
        groupId: Long,
        memberIds: List<Long>,
    ): MoilResult<Long> = when (
        val result = eventRemoteDataSource.createEvent(
            event.toCreateRequest(groupId, memberIds),
        )
    ) {
        is NetworkResult.Success -> MoilResult.Success(result.data.eventId)

        else -> result.mapToDomain { response -> response.eventId }
    }

    override suspend fun getEvent(eventId: Long): MoilResult<GroupEvent> = eventRemoteDataSource
        .getEvent(eventId)
        .mapToDomain(EventResponseDto::toDomain)

    override suspend fun updateEvent(
        eventId: Long,
        event: GroupEvent,
        memberIds: List<Long>,
    ): MoilResult<Unit> = when (
        val result = eventRemoteDataSource.updateEvent(
            eventId,
            event.toUpdateRequest(memberIds),
        )
    ) {
        is NetworkResult.Success -> MoilResult.Success(Unit)

        else -> result.mapToDomain { Unit }
    }

    override suspend fun deleteEvent(eventId: Long): MoilResult<Unit> = when (
        val result = eventRemoteDataSource.deleteEvent(eventId)
    ) {
        is NetworkResult.Success -> MoilResult.Success(Unit)

        else -> result.mapToDomain { Unit }
    }
}

private fun EventResponseDto.toDomain(): GroupEvent = GroupEvent(
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

private fun EventMemberResponseDto.toDomain(): EventMember = EventMember(
    userId = userId,
    nickname = nickname,
    color = colorId.toGroupColor(),
)

private fun GroupEvent.toCreateRequest(
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

private fun GroupEvent.toUpdateRequest(memberIds: List<Long>): UpdateEventRequestDto = UpdateEventRequestDto(
    title = title,
    startDate = startDate,
    endDate = endDate,
    isAllDay = isAllDay,
    startTime = startTime,
    endTime = endTime,
    location = location,
    sharedMemberIds = memberIds,
)
