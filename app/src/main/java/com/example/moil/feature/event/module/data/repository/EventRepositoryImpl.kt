package com.example.moil.feature.event.module.data.repository

import com.example.moil.core.domain.MoilResult
import com.example.moil.core.domain.mapToDomain
import com.example.moil.core.network.NetworkResult
import com.example.moil.feature.event.module.data.dto.EventResponseDto
import com.example.moil.feature.event.module.data.mapper.toCreateRequest
import com.example.moil.feature.event.module.data.mapper.toDomain
import com.example.moil.feature.event.module.data.mapper.toUpdateRequest
import com.example.moil.feature.event.module.data.remote.EventRemoteDataSource
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.event.module.domain.repository.EventRepository
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
