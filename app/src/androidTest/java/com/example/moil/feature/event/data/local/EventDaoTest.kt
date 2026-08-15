package com.example.moil.feature.event.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.time.YearMonth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventDaoTest {
    private lateinit var database: MoilDatabase
    private lateinit var eventLocalDataSource: EventLocalDataSource

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            MoilDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
        eventLocalDataSource = EventLocalDataSourceImpl(database.eventDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun 월별_조회와_건수는_같은_그룹과_해당_월의_일정만_반환한다() = runBlocking {
        eventLocalDataSource.upsertEvent(eventWithParticipants(1L, 10L, "2026-08-03", "GREEN"))
        eventLocalDataSource.upsertEvent(eventWithParticipants(2L, 10L, "2026-09-01", "RED"))
        eventLocalDataSource.upsertEvent(eventWithParticipants(3L, 20L, "2026-08-03", "VIOLET"))

        val augustEvents = eventLocalDataSource
            .observeEventsInMonth(10L, YearMonth.of(2026, 8))
            .first()
        val augustCount = eventLocalDataSource
            .observeEventCountInMonth(10L, YearMonth.of(2026, 8))
            .first()

        assertEquals(1, augustEvents.size)
        assertEquals("GREEN", augustEvents.single().participants.single().color)
        assertEquals(1, augustCount)
    }

    @Test
    fun 월별_캐시_교체는_선택_월_전후_한_달_외의_같은_그룹_일정을_삭제한다() = runBlocking {
        eventLocalDataSource.upsertEvent(eventWithParticipants(1L, 10L, "2026-06-01", "RED"))
        eventLocalDataSource.upsertEvent(eventWithParticipants(2L, 10L, "2026-07-01", "GREEN"))
        eventLocalDataSource.upsertEvent(eventWithParticipants(3L, 10L, "2026-08-01", "VIOLET"))
        eventLocalDataSource.upsertEvent(eventWithParticipants(4L, 10L, "2026-09-01", "YELLOW"))
        eventLocalDataSource.upsertEvent(eventWithParticipants(5L, 20L, "2026-06-01", "TEAL"))

        eventLocalDataSource.replaceEventsInMonth(
            groupId = 10L,
            month = YearMonth.of(2026, 8),
            events = listOf(eventWithParticipants(6L, 10L, "2026-08-02", "MAGENTA")),
            cacheStartMonth = YearMonth.of(2026, 7),
            cacheEndMonthExclusive = YearMonth.of(2026, 10),
        )

        val juneGroupEvents = eventLocalDataSource
            .observeEventsInMonth(10L, YearMonth.of(2026, 6))
            .first()
        val julyGroupEvents = eventLocalDataSource
            .observeEventsInMonth(10L, YearMonth.of(2026, 7))
            .first()
        val augustGroupEvents = eventLocalDataSource
            .observeEventsInMonth(10L, YearMonth.of(2026, 8))
            .first()
        val septemberGroupEvents = eventLocalDataSource
            .observeEventsInMonth(10L, YearMonth.of(2026, 9))
            .first()
        val juneOtherGroupEvents = eventLocalDataSource
            .observeEventsInMonth(20L, YearMonth.of(2026, 6))
            .first()

        assertEquals(emptyList<EventWithParticipants>(), juneGroupEvents)
        assertEquals(1, julyGroupEvents.size)
        assertEquals(listOf(6L), augustGroupEvents.map { it.event.eventId })
        assertEquals(1, septemberGroupEvents.size)
        assertEquals(1, juneOtherGroupEvents.size)
    }

    private fun eventWithParticipants(
        eventId: Long,
        groupId: Long,
        date: String,
        color: String,
    ): EventWithParticipants = EventWithParticipants(
        event = EventEntity(
            eventId = eventId,
            groupId = groupId,
            title = "일정 $eventId",
            date = date,
            isAllDay = true,
            startTime = null,
            endTime = null,
            location = null,
        ),
        participants = listOf(
            EventParticipantEntity(
                eventId = eventId,
                userId = 100L,
                nickname = "구성원",
                color = color,
            ),
        ),
    )
}
