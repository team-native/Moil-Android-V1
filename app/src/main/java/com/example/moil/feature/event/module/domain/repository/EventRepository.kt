package com.example.moil.feature.event.module.domain.repository

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.event.module.domain.model.EventAttendance
import com.example.moil.feature.event.module.domain.model.EventAttendanceChoice
import com.example.moil.feature.event.module.domain.model.EventAttendanceUpdate
import com.example.moil.feature.event.module.domain.model.EventAvailability
import com.example.moil.feature.event.module.domain.model.EventAvailabilitySummary
import com.example.moil.feature.event.module.domain.model.EventAvailabilityTimeSlot
import com.example.moil.feature.event.module.domain.model.MyEventAvailability
import java.time.YearMonth

interface EventRepository {
    suspend fun getGroupEvents(groupId: Long, month: YearMonth): MoilResult<List<GroupEvent>>
    suspend fun createEvent(event: GroupEvent, groupId: Long, memberIds: List<Long>): MoilResult<Long>
    suspend fun getEvent(eventId: Long): MoilResult<GroupEvent>
    suspend fun updateEvent(eventId: Long, event: GroupEvent, memberIds: List<Long>): MoilResult<Unit>
    suspend fun deleteEvent(eventId: Long): MoilResult<Unit>
    suspend fun getEventAttendance(eventId: Long): MoilResult<EventAttendance>
    suspend fun updateEventAttendance(
        eventId: Long,
        choice: EventAttendanceChoice,
    ): MoilResult<EventAttendanceUpdate>
    suspend fun deleteEventAttendance(eventId: Long): MoilResult<Unit>
    suspend fun getMyEventAvailability(eventId: Long, date: String): MoilResult<MyEventAvailability?>
    suspend fun getEventAvailability(eventId: Long, date: String): MoilResult<EventAvailability?>
    suspend fun getEventAvailabilitySummary(
        eventId: Long,
        date: String,
    ): MoilResult<EventAvailabilitySummary?>
    suspend fun updateMyEventAvailability(
        eventId: Long,
        date: String,
        timeSlots: List<EventAvailabilityTimeSlot>,
    ): MoilResult<Unit>
    suspend fun deleteMyEventAvailability(eventId: Long, date: String): MoilResult<Unit>
}
