package com.example.moil.feature.event.data.remote

import com.example.moil.core.network.ApiEnvelopeDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EventApiService {
    @GET("groups/{groupId}/events")
    suspend fun getGroupEvents(@Path("groupId") groupId: Long, @Query("month") month: String): Response<ApiEnvelopeDto<List<EventResponseDto>>>

    @POST("events")
    suspend fun createEvent(@Body request: EventRequestDto): Response<ApiEnvelopeDto<CreateEventResponseDto>>

    @GET("events/{eventId}")
    suspend fun getEvent(@Path("eventId") eventId: Long): Response<ApiEnvelopeDto<EventResponseDto>>

    @PATCH("events/{eventId}")
    suspend fun updateEvent(@Path("eventId") eventId: Long, @Body request: UpdateEventRequestDto): Response<ApiEnvelopeDto<Unit>>

    @DELETE("events/{eventId}")
    suspend fun deleteEvent(@Path("eventId") eventId: Long): Response<ApiEnvelopeDto<Unit>>
}
