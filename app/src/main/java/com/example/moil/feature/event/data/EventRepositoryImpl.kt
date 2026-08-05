package com.example.moil.feature.event.data

import com.example.moil.core.domain.MoilResult
import com.example.moil.core.domain.mapToDomain
import com.example.moil.core.network.NetworkResult
import com.example.moil.feature.event.data.local.EventEntity
import com.example.moil.feature.event.data.local.EventLocalDataSource
import com.example.moil.feature.event.data.local.EventParticipantEntity
import com.example.moil.feature.event.data.local.EventWithParticipants
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepositoryImpl @Inject constructor(
    private val eventRemoteDataSource: EventRemoteDataSource,
    private val eventLocalDataSource: EventLocalDataSource,
) : EventRepository {

    override fun observeGroupEvents(
        groupId: Long,
        month: YearMonth,
    ): Flow<List<GroupEvent>> = eventLocalDataSource
        .observeEventsInMonth(groupId, month)
        .map { events -> events.map(EventWithParticipants::toDomain) }

    override fun observeMonthlyEventCount(
        groupId: Long,
        month: YearMonth,
    ): Flow<Int> = eventLocalDataSource.observeEventCountInMonth(groupId, month)

    override suspend fun refreshGroupEvents(
        groupId: Long,
        month: YearMonth,
    ): MoilResult<Unit> = when (
        val result = eventRemoteDataSource.getGroupEvents(
            groupId = groupId,
            month = month.toString(),
        )
    ) {
        is NetworkResult.Success -> {
            eventLocalDataSource.replaceEventsInMonth(
                groupId = groupId,
                month = month,
                events = result.data.map { eventResponse ->
                    eventResponse.toLocal(groupId)
                },
            )
            MoilResult.Success(Unit)
        }

        else -> result.mapToDomain { Unit }
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
        is NetworkResult.Success -> {
            val eventId = result.data.eventId
            eventLocalDataSource.upsertEvent(
                event.copy(id = eventId).toLocal(groupId),
            )
            MoilResult.Success(eventId)
        }

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
        is NetworkResult.Success -> {
            eventLocalDataSource.findGroupId(eventId)?.let { groupId ->
                eventLocalDataSource.upsertEvent(
                    event.copy(id = eventId).toLocal(groupId),
                )
            }
            MoilResult.Success(Unit)
        }

        else -> result.mapToDomain { Unit }
    }

    override suspend fun deleteEvent(eventId: Long): MoilResult<Unit> = when (
        val result = eventRemoteDataSource.deleteEvent(eventId)
    ) {
        is NetworkResult.Success -> {
            eventLocalDataSource.deleteEvent(eventId)
            MoilResult.Success(Unit)
        }

        else -> result.mapToDomain { Unit }
    }
}

private fun EventResponseDto.toDomain(): GroupEvent = GroupEvent(
    id = eventId,
    title = title,
    date = date,
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

private fun EventResponseDto.toLocal(groupId: Long): EventWithParticipants = toDomain().toLocal(groupId)

private fun GroupEvent.toLocal(groupId: Long): EventWithParticipants = EventWithParticipants(
    event = EventEntity(
        eventId = id,
        groupId = groupId,
        title = title,
        date = date,
        isAllDay = isAllDay,
        startTime = startTime,
        endTime = endTime,
        location = location,
    ),
    participants = members.map { member ->
        EventParticipantEntity(
            eventId = id,
            userId = member.userId,
            nickname = member.nickname,
            color = member.color.toWireValueOrUnknown(),
        )
    },
)

private fun EventWithParticipants.toDomain(): GroupEvent = GroupEvent(
    id = event.eventId,
    title = event.title,
    date = event.date,
    isAllDay = event.isAllDay,
    startTime = event.startTime,
    endTime = event.endTime,
    location = event.location,
    members = participants.map { participant ->
        EventMember(
            userId = participant.userId,
            nickname = participant.nickname,
            color = participant.color.toGroupColor(),
        )
    },
)

private fun GroupEvent.toCreateRequest(
    groupId: Long,
    memberIds: List<Long>,
): EventRequestDto = EventRequestDto(
    groupId = groupId,
    title = title,
    date = date,
    isAllDay = isAllDay,
    startTime = startTime,
    endTime = endTime,
    location = location,
    sharedMemberIds = memberIds,
)

private fun GroupEvent.toUpdateRequest(memberIds: List<Long>): UpdateEventRequestDto = UpdateEventRequestDto(
    title = title,
    date = date,
    isAllDay = isAllDay,
    startTime = startTime,
    endTime = endTime,
    location = location,
    sharedMemberIds = memberIds,
)

private fun com.example.moil.feature.group.domain.GroupColor.toWireValueOrUnknown(): String =
    when (this) {
        com.example.moil.feature.group.domain.GroupColor.Sky -> "SKY"
        com.example.moil.feature.group.domain.GroupColor.Red -> "RED"
        com.example.moil.feature.group.domain.GroupColor.Green -> "GREEN"
        com.example.moil.feature.group.domain.GroupColor.Yellow -> "YELLOW"
        com.example.moil.feature.group.domain.GroupColor.Teal -> "TEAL"
        com.example.moil.feature.group.domain.GroupColor.Violet -> "VIOLET"
        com.example.moil.feature.group.domain.GroupColor.Magenta -> "MAGENTA"
        com.example.moil.feature.group.domain.GroupColor.Unknown -> "UNKNOWN"
    }
