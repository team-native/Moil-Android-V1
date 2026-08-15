package com.example.moil.feature.event.data.remote

import com.example.moil.core.network.ApiExecutor
import com.example.moil.core.network.NetworkResult
import javax.inject.Inject

class EventRemoteDataSourceImpl @Inject constructor(
    private val eventApiService: EventApiService,
    private val apiExecutor: ApiExecutor,
) : EventRemoteDataSource {
    override suspend fun getGroupEvents(groupId: Long, month: String): NetworkResult<List<EventResponseDto>> = apiExecutor.execute { eventApiService.getGroupEvents(groupId, month) }
    override suspend fun createEvent(request: EventRequestDto): NetworkResult<CreateEventResponseDto> = apiExecutor.execute { eventApiService.createEvent(request) }
    override suspend fun getEvent(eventId: Long): NetworkResult<EventResponseDto> = apiExecutor.execute { eventApiService.getEvent(eventId) }
    override suspend fun updateEvent(eventId: Long, request: UpdateEventRequestDto): NetworkResult<Unit> = apiExecutor.executeVoid { eventApiService.updateEvent(eventId, request) }
    override suspend fun deleteEvent(eventId: Long): NetworkResult<Unit> = apiExecutor.executeVoid { eventApiService.deleteEvent(eventId) }
}
