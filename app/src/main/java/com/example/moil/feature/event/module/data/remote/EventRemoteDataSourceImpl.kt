package com.example.moil.feature.event.module.data.remote

import com.example.moil.core.network.ApiExecutor
import com.example.moil.core.network.NetworkResult
import com.example.moil.feature.event.module.data.dto.CreateEventResponseDto
import com.example.moil.feature.event.module.data.dto.EventRequestDto
import com.example.moil.feature.event.module.data.dto.EventResponseDto
import com.example.moil.feature.event.module.data.dto.EventAttendanceResponseDto
import com.example.moil.feature.event.module.data.dto.EventAttendanceUpdateResponseDto
import com.example.moil.feature.event.module.data.dto.EventAvailabilityResponseDto
import com.example.moil.feature.event.module.data.dto.EventAvailabilitySummaryResponseDto
import com.example.moil.feature.event.module.data.dto.MyEventAvailabilityResponseDto
import com.example.moil.feature.event.module.data.dto.UpdateEventAttendanceRequestDto
import com.example.moil.feature.event.module.data.dto.UpdateEventAvailabilityRequestDto
import com.example.moil.feature.event.module.data.dto.UpdateEventRequestDto
import javax.inject.Inject

class EventRemoteDataSourceImpl @Inject constructor(
    private val eventApiService: EventApiService,
    private val apiExecutor: ApiExecutor,
) : EventRemoteDataSource {
    override suspend fun getGroupEvents(
        groupId: Long,
        month: String,
    ): NetworkResult<List<EventResponseDto>> = apiExecutor.execute {
        eventApiService.getGroupEvents(groupId, month)
    }

    override suspend fun createEvent(
        request: EventRequestDto,
    ): NetworkResult<CreateEventResponseDto> = apiExecutor.execute {
        eventApiService.createEvent(request)
    }

    override suspend fun getEvent(
        eventId: Long,
    ): NetworkResult<EventResponseDto> = apiExecutor.execute {
        eventApiService.getEvent(eventId)
    }

    override suspend fun updateEvent(
        eventId: Long,
        request: UpdateEventRequestDto,
    ): NetworkResult<Unit> = apiExecutor.executeVoid {
        eventApiService.updateEvent(eventId, request)
    }

    override suspend fun deleteEvent(eventId: Long): NetworkResult<Unit> = apiExecutor.executeVoid {
        eventApiService.deleteEvent(eventId)
    }

    override suspend fun getEventAttendance(
        eventId: Long,
    ): NetworkResult<EventAttendanceResponseDto> = apiExecutor.execute {
        eventApiService.getEventAttendance(eventId)
    }

    override suspend fun updateEventAttendance(
        eventId: Long,
        request: UpdateEventAttendanceRequestDto,
    ): NetworkResult<EventAttendanceUpdateResponseDto> = apiExecutor.execute {
        eventApiService.updateEventAttendance(eventId, request)
    }

    override suspend fun deleteEventAttendance(eventId: Long): NetworkResult<Unit> = apiExecutor.executeVoid {
        eventApiService.deleteEventAttendance(eventId)
    }

    override suspend fun getMyEventAvailability(
        eventId: Long,
        date: String,
    ): NetworkResult<MyEventAvailabilityResponseDto?> = apiExecutor.executeNullable {
        eventApiService.getMyEventAvailability(eventId, date)
    }

    override suspend fun getEventAvailability(
        eventId: Long,
        date: String,
    ): NetworkResult<EventAvailabilityResponseDto?> = apiExecutor.executeNullable {
        eventApiService.getEventAvailability(eventId, date)
    }

    override suspend fun getEventAvailabilitySummary(
        eventId: Long,
        date: String,
    ): NetworkResult<EventAvailabilitySummaryResponseDto?> = apiExecutor.executeNullable {
        eventApiService.getEventAvailabilitySummary(eventId, date)
    }

    override suspend fun updateMyEventAvailability(
        eventId: Long,
        request: UpdateEventAvailabilityRequestDto,
    ): NetworkResult<Unit> = apiExecutor.executeVoid {
        eventApiService.updateMyEventAvailability(eventId, request)
    }

    override suspend fun deleteMyEventAvailability(
        eventId: Long,
        date: String,
    ): NetworkResult<Unit> = apiExecutor.executeVoid {
        eventApiService.deleteMyEventAvailability(eventId, date)
    }
}
