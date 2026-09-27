package com.example.moil.feature.event.module.data.remote

import com.example.moil.core.network.ApiEnvelopeDto
import com.example.moil.feature.event.module.data.dto.EventAttendanceResponseDto
import com.example.moil.feature.event.module.data.dto.EventAttendanceUpdateResponseDto
import com.example.moil.feature.event.module.data.dto.EventAvailabilityResponseDto
import com.example.moil.feature.event.module.data.dto.EventAvailabilitySummaryResponseDto
import com.example.moil.feature.event.module.data.dto.CreateEventResponseDto
import com.example.moil.feature.event.module.data.dto.EventRequestDto
import com.example.moil.feature.event.module.data.dto.EventResponseDto
import com.example.moil.feature.event.module.data.dto.MyEventAvailabilityResponseDto
import com.example.moil.feature.event.module.data.dto.UpdateEventAttendanceRequestDto
import com.example.moil.feature.event.module.data.dto.UpdateEventAvailabilityRequestDto
import com.example.moil.feature.event.module.data.dto.UpdateEventRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT
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

    @GET("events/{eventId}/attendance")
    suspend fun getEventAttendance(
        @Path("eventId") eventId: Long,
    ): Response<ApiEnvelopeDto<EventAttendanceResponseDto>>

    @PUT("events/{eventId}/attendance")
    suspend fun updateEventAttendance(
        @Path("eventId") eventId: Long,
        @Body request: UpdateEventAttendanceRequestDto,
    ): Response<ApiEnvelopeDto<EventAttendanceUpdateResponseDto>>

    @DELETE("events/{eventId}/attendance")
    suspend fun deleteEventAttendance(
        @Path("eventId") eventId: Long,
    ): Response<ApiEnvelopeDto<Unit>>

    @GET("events/{eventId}/availability/me")
    suspend fun getMyEventAvailability(
        @Path("eventId") eventId: Long,
        @Query("date") date: String,
    ): Response<ApiEnvelopeDto<MyEventAvailabilityResponseDto?>>

    @GET("events/{eventId}/availability")
    suspend fun getEventAvailability(
        @Path("eventId") eventId: Long,
        @Query("date") date: String,
    ): Response<ApiEnvelopeDto<EventAvailabilityResponseDto?>>

    @GET("events/{eventId}/availability/summary")
    suspend fun getEventAvailabilitySummary(
        @Path("eventId") eventId: Long,
        @Query("date") date: String,
    ): Response<ApiEnvelopeDto<EventAvailabilitySummaryResponseDto?>>

    @PUT("events/{eventId}/availability")
    suspend fun updateMyEventAvailability(
        @Path("eventId") eventId: Long,
        @Body request: UpdateEventAvailabilityRequestDto,
    ): Response<ApiEnvelopeDto<Unit>>

    @DELETE("events/{eventId}/availability")
    suspend fun deleteMyEventAvailability(
        @Path("eventId") eventId: Long,
        @Query("date") date: String,
    ): Response<ApiEnvelopeDto<Unit>>
}
