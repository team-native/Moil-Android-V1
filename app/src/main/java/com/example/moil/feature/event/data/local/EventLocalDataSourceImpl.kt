package com.example.moil.feature.event.data.local

import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class EventLocalDataSourceImpl @Inject constructor(
    private val eventDao: EventDao,
) : EventLocalDataSource {

    override fun observeEventsInMonth(
        groupId: Long,
        month: YearMonth,
    ): Flow<List<EventWithParticipants>> = eventDao.observeEventsInMonth(
        groupId = groupId,
        startDate = month.atDay(1).toString(),
        endDateExclusive = month.plusMonths(1).atDay(1).toString(),
    )

    override fun observeEventCountInMonth(
        groupId: Long,
        month: YearMonth,
    ): Flow<Int> = eventDao.observeEventCountInMonth(
        groupId = groupId,
        startDate = month.atDay(1).toString(),
        endDateExclusive = month.plusMonths(1).atDay(1).toString(),
    )

    override suspend fun getEventsInMonth(
        groupId: Long,
        month: YearMonth,
    ): List<EventWithParticipants> = eventDao.getEventsInMonth(
        groupId = groupId,
        startDate = month.atDay(1).toString(),
        endDateExclusive = month.plusMonths(1).atDay(1).toString(),
    )

    override suspend fun replaceEventsInMonth(
        groupId: Long,
        month: YearMonth,
        events: List<EventWithParticipants>,
        cacheStartMonth: YearMonth,
        cacheEndMonthExclusive: YearMonth,
    ) {
        eventDao.replaceEventsInMonth(
            groupId = groupId,
            startDate = month.atDay(1).toString(),
            endDateExclusive = month.plusMonths(1).atDay(1).toString(),
            events = events,
            cacheStartDate = cacheStartMonth.atDay(1).toString(),
            cacheEndDateExclusive = cacheEndMonthExclusive.atDay(1).toString(),
        )
    }

    override suspend fun pruneEventsOutsideCacheWindow(
        groupId: Long,
        cacheStartMonth: YearMonth,
        cacheEndMonthExclusive: YearMonth,
    ) {
        eventDao.pruneEventsOutsideCacheWindow(
            groupId = groupId,
            cacheStartDate = cacheStartMonth.atDay(1).toString(),
            cacheEndDateExclusive = cacheEndMonthExclusive.atDay(1).toString(),
        )
    }

    override suspend fun upsertEvent(event: EventWithParticipants) {
        eventDao.upsertEvent(event)
    }

    override suspend fun deleteEvent(eventId: Long) {
        eventDao.deleteEvent(eventId)
    }

    override suspend fun findGroupId(eventId: Long): Long? = eventDao.findGroupId(eventId)
}
