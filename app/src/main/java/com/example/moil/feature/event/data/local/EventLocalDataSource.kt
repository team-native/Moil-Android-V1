package com.example.moil.feature.event.data.local

import java.time.YearMonth
import kotlinx.coroutines.flow.Flow

interface EventLocalDataSource {
    fun observeEventsInMonth(
        groupId: Long,
        month: YearMonth,
    ): Flow<List<EventWithParticipants>>

    fun observeEventCountInMonth(
        groupId: Long,
        month: YearMonth,
    ): Flow<Int>

    suspend fun replaceEventsInMonth(
        groupId: Long,
        month: YearMonth,
        events: List<EventWithParticipants>,
    )

    suspend fun upsertEvent(event: EventWithParticipants)

    suspend fun deleteEvent(eventId: Long)

    suspend fun findGroupId(eventId: Long): Long?
}
