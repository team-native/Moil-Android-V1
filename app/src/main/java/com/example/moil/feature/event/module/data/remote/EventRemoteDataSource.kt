package com.example.moil.feature.event.module.data.remote

import com.example.moil.core.network.NetworkResult
import com.example.moil.feature.event.module.data.dto.CreateEventResponseDto
import com.example.moil.feature.event.module.data.dto.EventRequestDto
import com.example.moil.feature.event.module.data.dto.EventResponseDto
import com.example.moil.feature.event.module.data.dto.UpdateEventRequestDto

interface EventRemoteDataSource {
    suspend fun getGroupEvents(groupId: Long, month: String): NetworkResult<List<EventResponseDto>>
    suspend fun createEvent(request: EventRequestDto): NetworkResult<CreateEventResponseDto>
    suspend fun getEvent(eventId: Long): NetworkResult<EventResponseDto>
    suspend fun updateEvent(eventId: Long, request: UpdateEventRequestDto): NetworkResult<Unit>
    suspend fun deleteEvent(eventId: Long): NetworkResult<Unit>
}
