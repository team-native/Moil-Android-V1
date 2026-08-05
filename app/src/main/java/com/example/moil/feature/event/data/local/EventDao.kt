package com.example.moil.feature.event.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class EventDao {

    @Transaction
    @Query(
        """
        SELECT * FROM events
        WHERE group_id = :groupId
          AND date >= :startDate
          AND date < :endDateExclusive
        ORDER BY date ASC, start_time ASC
        """,
    )
    abstract fun observeEventsInMonth(
        groupId: Long,
        startDate: String,
        endDateExclusive: String,
    ): Flow<List<EventWithParticipants>>

    @Query(
        """
        SELECT COUNT(*) FROM events
        WHERE group_id = :groupId
          AND date >= :startDate
          AND date < :endDateExclusive
        """,
    )
    abstract fun observeEventCountInMonth(
        groupId: Long,
        startDate: String,
        endDateExclusive: String,
    ): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertEvents(events: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertParticipants(participants: List<EventParticipantEntity>)

    @Query("DELETE FROM events WHERE event_id = :eventId")
    abstract suspend fun deleteEvent(eventId: Long)

    @Query("DELETE FROM event_participants WHERE event_id = :eventId")
    protected abstract suspend fun deleteParticipantsForEvent(eventId: Long)

    @Query(
        """
        DELETE FROM events
        WHERE group_id = :groupId
          AND date >= :startDate
          AND date < :endDateExclusive
        """,
    )
    protected abstract suspend fun deleteEventsInMonth(
        groupId: Long,
        startDate: String,
        endDateExclusive: String,
    )

    @Query("SELECT group_id FROM events WHERE event_id = :eventId LIMIT 1")
    abstract suspend fun findGroupId(eventId: Long): Long?

    @Transaction
    open suspend fun replaceEventsInMonth(
        groupId: Long,
        startDate: String,
        endDateExclusive: String,
        events: List<EventWithParticipants>,
    ) {
        deleteEventsInMonth(groupId, startDate, endDateExclusive)
        insertEvents(events.map(EventWithParticipants::event))
        insertParticipants(events.flatMap(EventWithParticipants::participants))
    }

    @Transaction
    open suspend fun upsertEvent(eventWithParticipants: EventWithParticipants) {
        deleteParticipantsForEvent(eventWithParticipants.event.eventId)
        insertEvents(listOf(eventWithParticipants.event))
        insertParticipants(eventWithParticipants.participants)
    }
}
