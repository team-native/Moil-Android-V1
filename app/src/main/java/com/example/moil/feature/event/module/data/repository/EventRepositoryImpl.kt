package com.example.moil.feature.event.module.data.repository

import com.example.moil.core.domain.MoilResult
import com.example.moil.core.domain.mapToDomain
import com.example.moil.core.network.NetworkResult
import com.example.moil.feature.event.module.data.dto.EventResponseDto
import com.example.moil.feature.event.module.data.mapper.toCreateRequest
import com.example.moil.feature.event.module.data.mapper.toEventAttendanceDomain
import com.example.moil.feature.event.module.data.mapper.toEventAttendanceUpdateDomain
import com.example.moil.feature.event.module.data.mapper.toEventAvailabilityDomain
import com.example.moil.feature.event.module.data.mapper.toEventAvailabilitySummaryDomain
import com.example.moil.feature.event.module.data.mapper.toMyEventAvailabilityDomain
import com.example.moil.feature.event.module.data.mapper.toRequest
import com.example.moil.feature.event.module.data.mapper.toDomain
import com.example.moil.feature.event.module.data.mapper.toUpdateRequest
import com.example.moil.feature.event.module.data.remote.EventRemoteDataSource
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.event.module.domain.model.EventAttendanceChoice
import com.example.moil.feature.event.module.domain.model.EventAttendance
import com.example.moil.feature.event.module.domain.model.EventAttendanceUpdate
import com.example.moil.feature.event.module.domain.model.EventAvailability
import com.example.moil.feature.event.module.domain.model.EventAvailabilitySummary
import com.example.moil.feature.event.module.domain.model.EventAvailabilityTimeSlot
import com.example.moil.feature.event.module.domain.model.MyEventAvailability
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

    override suspend fun getEventAttendance(eventId: Long): MoilResult<EventAttendance> =
        eventRemoteDataSource.getEventAttendance(eventId)
            .mapToDomain { attendance -> attendance.toEventAttendanceDomain() }

    override suspend fun updateEventAttendance(
        eventId: Long,
        choice: EventAttendanceChoice,
    ): MoilResult<EventAttendanceUpdate> = eventRemoteDataSource.updateEventAttendance(
        eventId = eventId,
        request = choice.toRequest(),
    ).mapToDomain { attendanceUpdate -> attendanceUpdate.toEventAttendanceUpdateDomain() }

    override suspend fun deleteEventAttendance(eventId: Long): MoilResult<Unit> =
        eventRemoteDataSource.deleteEventAttendance(eventId)
            .mapToDomain { Unit }

    override suspend fun getMyEventAvailability(
        eventId: Long,
        date: String,
    ): MoilResult<MyEventAvailability?> = eventRemoteDataSource.getMyEventAvailability(eventId, date)
        .mapToDomain { availability -> availability?.toMyEventAvailabilityDomain() }

    override suspend fun getEventAvailability(
        eventId: Long,
        date: String,
    ): MoilResult<EventAvailability?> = eventRemoteDataSource.getEventAvailability(eventId, date)
        .mapToDomain { availability -> availability?.toEventAvailabilityDomain() }

    override suspend fun getEventAvailabilitySummary(
        eventId: Long,
        date: String,
    ): MoilResult<EventAvailabilitySummary?> = eventRemoteDataSource.getEventAvailabilitySummary(eventId, date)
        .mapToDomain { summary -> summary?.toEventAvailabilitySummaryDomain() }

    override suspend fun updateMyEventAvailability(
        eventId: Long,
        date: String,
        timeSlots: List<EventAvailabilityTimeSlot>,
    ): MoilResult<Unit> = eventRemoteDataSource.updateMyEventAvailability(
        eventId = eventId,
        request = timeSlots.toRequest(date),
    ).mapToDomain { Unit }

    override suspend fun deleteMyEventAvailability(eventId: Long, date: String): MoilResult<Unit> =
        eventRemoteDataSource.deleteMyEventAvailability(eventId, date)
            .mapToDomain { Unit }
}
