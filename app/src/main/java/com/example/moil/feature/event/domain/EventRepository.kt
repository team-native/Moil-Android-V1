package com.example.moil.feature.event.domain

import com.example.moil.core.domain.MoilResult
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun observeGroupEvents(groupId: Long, month: YearMonth): Flow<List<GroupEvent>>
    fun observeMonthlyEventCount(groupId: Long, month: YearMonth): Flow<Int>
    suspend fun refreshGroupEvents(groupId: Long, month: YearMonth): MoilResult<Unit>
    suspend fun createEvent(event: GroupEvent, groupId: Long, memberIds: List<Long>): MoilResult<Long>
    suspend fun getEvent(eventId: Long): MoilResult<GroupEvent>
    suspend fun updateEvent(eventId: Long, event: GroupEvent, memberIds: List<Long>): MoilResult<Unit>
    suspend fun deleteEvent(eventId: Long): MoilResult<Unit>
}
