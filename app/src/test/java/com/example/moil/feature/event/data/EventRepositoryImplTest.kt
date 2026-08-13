package com.example.moil.feature.event.data

import com.example.moil.core.domain.MoilResult
import com.example.moil.core.network.NetworkResult
import com.example.moil.feature.event.data.local.EventEntity
import com.example.moil.feature.event.data.local.EventLocalDataSource
import com.example.moil.feature.event.data.local.EventParticipantEntity
import com.example.moil.feature.event.data.local.EventWithParticipants
import com.example.moil.feature.event.data.remote.CreateEventResponseDto
import com.example.moil.feature.event.data.remote.EventRemoteDataSource
import com.example.moil.feature.event.data.remote.EventRequestDto
import com.example.moil.feature.event.data.remote.EventResponseDto
import com.example.moil.feature.event.data.remote.UpdateEventRequestDto
import com.example.moil.feature.event.domain.EventMember
import com.example.moil.feature.event.domain.GroupEvent
import com.example.moil.feature.group.domain.GroupColor
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventRepositoryImplTest {

    @Test
    fun `서버 월별 조회 성공은 Room 월 캐시를 교체한다`() = runBlocking {
        val localDataSource = FakeEventLocalDataSource().apply {
            upsertEvent(localEvent(1L, 10L, "2026-08-01", "기존 일정"))
        }
        val remoteDataSource = FakeEventRemoteDataSource(
            groupEventsResult = NetworkResult.Success(
                listOf(eventResponse(2L, "서버 일정", "2026-08-03")),
            ),
        )
        val repository = EventRepositoryImpl(remoteDataSource, localDataSource)

        val result = repository.refreshGroupEvents(10L, YearMonth.of(2026, 8))
        val cachedEvents = repository
            .observeGroupEvents(10L, YearMonth.of(2026, 8))
            .first()

        assertEquals(MoilResult.Success(Unit), result)
        assertEquals(listOf("서버 일정"), cachedEvents.map(GroupEvent::title))
    }

    @Test
    fun `서버 월별 조회 결과가 Room과 같으면 월 캐시를 교체하지 않는다`() = runBlocking {
        val localDataSource = FakeEventLocalDataSource().apply {
            upsertEvent(localEvent(2L, 10L, "2026-08-03", "서버 일정"))
        }
        val repository = EventRepositoryImpl(
            FakeEventRemoteDataSource(
                groupEventsResult = NetworkResult.Success(
                    listOf(eventResponse(2L, "서버 일정", "2026-08-03")),
                ),
            ),
            localDataSource,
        )

        val result = repository.refreshGroupEvents(10L, YearMonth.of(2026, 8))

        assertEquals(MoilResult.Success(Unit), result)
        assertEquals(0, localDataSource.replaceEventsInMonthCallCount)
        assertEquals(1, localDataSource.pruneEventsOutsideCacheWindowCallCount)
    }

    @Test
    fun `서버 조회 실패는 기존 Room 캐시를 유지한다`() = runBlocking {
        val localDataSource = FakeEventLocalDataSource().apply {
            upsertEvent(localEvent(1L, 10L, "2026-08-01", "기존 일정"))
        }
        val repository = EventRepositoryImpl(
            FakeEventRemoteDataSource(
                groupEventsResult = NetworkResult.NetworkError(IllegalStateException("offline")),
            ),
            localDataSource,
        )

        val result = repository.refreshGroupEvents(10L, YearMonth.of(2026, 8))
        val cachedEvents = repository
            .observeGroupEvents(10L, YearMonth.of(2026, 8))
            .first()

        assertTrue(result is MoilResult.Failure)
        assertEquals(listOf("기존 일정"), cachedEvents.map(GroupEvent::title))
    }

    @Test
    fun `일정 생성 성공은 참여자 색과 함께 Room에 저장한다`() = runBlocking {
        val localDataSource = FakeEventLocalDataSource()
        val repository = EventRepositoryImpl(
            FakeEventRemoteDataSource(
                createEventResult = NetworkResult.Success(CreateEventResponseDto(99L)),
            ),
            localDataSource,
        )
        val event = GroupEvent(
            id = 0L,
            title = "저녁 식사",
            date = "2026-08-03",
            isAllDay = false,
            startTime = "18:00",
            endTime = "19:00",
            location = null,
            members = listOf(EventMember(1L, "초록", GroupColor.Green)),
        )

        val result = repository.createEvent(event, 10L, listOf(1L))
        val cachedEvent = repository
            .observeGroupEvents(10L, YearMonth.of(2026, 8))
            .first()
            .single()

        assertEquals(MoilResult.Success(99L), result)
        assertEquals(99L, cachedEvent.id)
        assertEquals(GroupColor.Green, cachedEvent.members.single().color)
    }

    private fun eventResponse(
        eventId: Long,
        title: String,
        date: String,
    ): EventResponseDto = EventResponseDto(
        eventId = eventId,
        title = title,
        date = date,
        isAllDay = true,
        members = emptyList(),
    )

    private fun localEvent(
        eventId: Long,
        groupId: Long,
        date: String,
        title: String,
    ): EventWithParticipants = EventWithParticipants(
        event = EventEntity(
            eventId = eventId,
            groupId = groupId,
            title = title,
            date = date,
            isAllDay = true,
            startTime = null,
            endTime = null,
            location = null,
        ),
        participants = emptyList(),
    )
}

private class FakeEventLocalDataSource : EventLocalDataSource {
    private val events = MutableStateFlow<List<EventWithParticipants>>(emptyList())
    var replaceEventsInMonthCallCount = 0
        private set
    var pruneEventsOutsideCacheWindowCallCount = 0
        private set

    override fun observeEventsInMonth(
        groupId: Long,
        month: YearMonth,
    ): Flow<List<EventWithParticipants>> = MutableStateFlow(
        events.value.filter { eventWithParticipants ->
            eventWithParticipants.event.groupId == groupId &&
                eventWithParticipants.event.date.startsWith(month.toString())
        },
    )

    override fun observeEventCountInMonth(groupId: Long, month: YearMonth): Flow<Int> =
        MutableStateFlow(
            events.value.count { eventWithParticipants ->
                eventWithParticipants.event.groupId == groupId &&
                    eventWithParticipants.event.date.startsWith(month.toString())
            },
        )

    override suspend fun getEventsInMonth(
        groupId: Long,
        month: YearMonth,
    ): List<EventWithParticipants> = events.value.filter { eventWithParticipants ->
        eventWithParticipants.event.groupId == groupId &&
            eventWithParticipants.event.date.startsWith(month.toString())
    }

    override suspend fun replaceEventsInMonth(
        groupId: Long,
        month: YearMonth,
        events: List<EventWithParticipants>,
        cacheStartMonth: YearMonth,
        cacheEndMonthExclusive: YearMonth,
    ) {
        replaceEventsInMonthCallCount++
        val replacedEvents = this.events.value.filterNot { eventWithParticipants ->
            eventWithParticipants.event.groupId == groupId &&
                eventWithParticipants.event.date.startsWith(month.toString())
        } + events
        this.events.value = replacedEvents.filter { eventWithParticipants ->
            eventWithParticipants.event.groupId != groupId ||
                (
                    eventWithParticipants.event.date >= cacheStartMonth.atDay(1).toString() &&
                        eventWithParticipants.event.date < cacheEndMonthExclusive.atDay(1).toString()
                    )
        }
    }

    override suspend fun pruneEventsOutsideCacheWindow(
        groupId: Long,
        cacheStartMonth: YearMonth,
        cacheEndMonthExclusive: YearMonth,
    ) {
        pruneEventsOutsideCacheWindowCallCount++
        events.value = events.value.filter { eventWithParticipants ->
            eventWithParticipants.event.groupId != groupId ||
                (
                    eventWithParticipants.event.date >= cacheStartMonth.atDay(1).toString() &&
                        eventWithParticipants.event.date < cacheEndMonthExclusive.atDay(1).toString()
                    )
        }
    }

    override suspend fun upsertEvent(event: EventWithParticipants) {
        events.value = events.value.filterNot { eventWithParticipants ->
            eventWithParticipants.event.eventId == event.event.eventId
        } + event
    }

    override suspend fun deleteEvent(eventId: Long) {
        events.value = events.value.filterNot { eventWithParticipants ->
            eventWithParticipants.event.eventId == eventId
        }
    }

    override suspend fun findGroupId(eventId: Long): Long? = events.value
        .firstOrNull { eventWithParticipants ->
            eventWithParticipants.event.eventId == eventId
        }
        ?.event
        ?.groupId
}

private class FakeEventRemoteDataSource(
    private val groupEventsResult: NetworkResult<List<EventResponseDto>> = NetworkResult.Success(emptyList()),
    private val createEventResult: NetworkResult<CreateEventResponseDto> =
        NetworkResult.Success(CreateEventResponseDto(1L)),
) : EventRemoteDataSource {

    override suspend fun getGroupEvents(
        groupId: Long,
        month: String,
    ): NetworkResult<List<EventResponseDto>> = groupEventsResult

    override suspend fun createEvent(
        request: EventRequestDto,
    ): NetworkResult<CreateEventResponseDto> = createEventResult

    override suspend fun getEvent(eventId: Long): NetworkResult<EventResponseDto> =
        NetworkResult.NetworkError(IllegalStateException("not used"))

    override suspend fun updateEvent(
        eventId: Long,
        request: UpdateEventRequestDto,
    ): NetworkResult<Unit> = NetworkResult.NetworkError(IllegalStateException("not used"))

    override suspend fun deleteEvent(eventId: Long): NetworkResult<Unit> =
        NetworkResult.NetworkError(IllegalStateException("not used"))
}
