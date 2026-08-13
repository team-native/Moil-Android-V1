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
    abstract suspend fun getEventsInMonth(
        groupId: Long,
        startDate: String,
        endDateExclusive: String,
    ): List<EventWithParticipants>

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

    @Query(
        """
        DELETE FROM events
        WHERE group_id = :groupId
          AND (date < :cacheStartDate OR date >= :cacheEndDateExclusive)
        """,
    )
    protected abstract suspend fun deleteEventsOutsideCacheWindow(
        groupId: Long,
        cacheStartDate: String,
        cacheEndDateExclusive: String,
    )

    @Query("SELECT group_id FROM events WHERE event_id = :eventId LIMIT 1")
    abstract suspend fun findGroupId(eventId: Long): Long?

    @Transaction
    open suspend fun replaceEventsInMonth(
        groupId: Long,
        startDate: String,
        endDateExclusive: String,
        events: List<EventWithParticipants>,
        cacheStartDate: String,
        cacheEndDateExclusive: String,
    ) {
        deleteEventsInMonth(groupId, startDate, endDateExclusive)
        insertEvents(events.map(EventWithParticipants::event))
        insertParticipants(events.flatMap(EventWithParticipants::participants))
        // 월별 동기화가 끝난 뒤 선택 월 전후 한 달만 로컬 캐시로 유지합니다.
        deleteEventsOutsideCacheWindow(groupId, cacheStartDate, cacheEndDateExclusive)
    }

    /** 선택한 3개월 보관 범위 밖의 같은 그룹 일정만 삭제합니다. */
    @Transaction
    open suspend fun pruneEventsOutsideCacheWindow(
        groupId: Long,
        cacheStartDate: String,
        cacheEndDateExclusive: String,
    ) {
        deleteEventsOutsideCacheWindow(groupId, cacheStartDate, cacheEndDateExclusive)
    }

    @Transaction
    open suspend fun upsertEvent(eventWithParticipants: EventWithParticipants) {
        deleteParticipantsForEvent(eventWithParticipants.event.eventId)
        insertEvents(listOf(eventWithParticipants.event))
        insertParticipants(eventWithParticipants.participants)
    }
}
