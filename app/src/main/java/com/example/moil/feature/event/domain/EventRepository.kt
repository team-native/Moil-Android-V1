package com.example.moil.feature.event.domain

import com.example.moil.core.domain.MoilResult
import java.time.YearMonth

interface EventRepository {
    suspend fun getGroupEvents(groupId: Long, month: YearMonth): MoilResult<List<GroupEvent>>
    suspend fun createEvent(event: GroupEvent, groupId: Long, memberIds: List<Long>): MoilResult<Long>
    suspend fun getEvent(eventId: Long): MoilResult<GroupEvent>
    suspend fun updateEvent(eventId: Long, event: GroupEvent, memberIds: List<Long>): MoilResult<Unit>
    suspend fun deleteEvent(eventId: Long): MoilResult<Unit>
}
