package com.example.moil.feature.event.module.data.remote

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

interface EventRemoteDataSource {
    suspend fun getGroupEvents(groupId: Long, month: String): NetworkResult<List<EventResponseDto>>
    suspend fun createEvent(request: EventRequestDto): NetworkResult<CreateEventResponseDto>
    suspend fun getEvent(eventId: Long): NetworkResult<EventResponseDto>
    suspend fun updateEvent(eventId: Long, request: UpdateEventRequestDto): NetworkResult<Unit>
    suspend fun deleteEvent(eventId: Long): NetworkResult<Unit>
    suspend fun getEventAttendance(eventId: Long): NetworkResult<EventAttendanceResponseDto>
    suspend fun updateEventAttendance(
        eventId: Long,
        request: UpdateEventAttendanceRequestDto,
    ): NetworkResult<EventAttendanceUpdateResponseDto>
    suspend fun deleteEventAttendance(eventId: Long): NetworkResult<Unit>
    suspend fun getMyEventAvailability(
        eventId: Long,
        date: String,
    ): NetworkResult<MyEventAvailabilityResponseDto?>
    suspend fun getEventAvailability(
        eventId: Long,
        date: String,
    ): NetworkResult<EventAvailabilityResponseDto?>
    suspend fun getEventAvailabilitySummary(
        eventId: Long,
        date: String,
    ): NetworkResult<EventAvailabilitySummaryResponseDto?>
    suspend fun updateMyEventAvailability(
        eventId: Long,
        request: UpdateEventAvailabilityRequestDto,
    ): NetworkResult<Unit>
    suspend fun deleteMyEventAvailability(eventId: Long, date: String): NetworkResult<Unit>
}
