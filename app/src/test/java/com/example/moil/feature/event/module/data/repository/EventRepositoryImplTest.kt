package com.example.moil.feature.event.module.data.repository

import com.example.moil.core.domain.MoilResult
import com.example.moil.core.network.NetworkResult
import com.example.moil.feature.event.module.data.dto.CreateEventResponseDto
import com.example.moil.feature.event.module.data.dto.EventMemberResponseDto
import com.example.moil.feature.event.module.data.dto.EventRequestDto
import com.example.moil.feature.event.module.data.dto.EventResponseDto
import com.example.moil.feature.event.module.data.dto.UpdateEventRequestDto
import com.example.moil.feature.event.module.data.remote.EventRemoteDataSource
import com.example.moil.feature.event.module.domain.model.EventMember
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.group.domain.GroupColor
import java.time.YearMonth
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventRepositoryImplTest {

    @Test
    fun `서버 월별 조회 성공은 Domain 일정 목록으로 변환한다`() = runBlocking {
        val repository = EventRepositoryImpl(
            FakeEventRemoteDataSource(
                groupEventsResult = NetworkResult.Success(
                    listOf(eventResponse(eventId = 2L, title = "서버 일정")),
                ),
            ),
        )

        val result = repository.getGroupEvents(10L, YearMonth.of(2026, 8))

        assertEquals(
            MoilResult.Success(
                listOf(
                    GroupEvent(
                        id = 2L,
                        title = "서버 일정",
                        startDate = "2026-08-03",
                        endDate = "2026-08-05",
                        isAllDay = true,
                        startTime = null,
                        endTime = null,
                        location = null,
                        members = listOf(EventMember(1L, "초록", GroupColor.Green)),
                    ),
                ),
            ),
            result,
        )
    }

    @Test
    fun `서버 월별 조회 실패는 실패 결과를 반환한다`() = runBlocking {
        val repository = EventRepositoryImpl(
            FakeEventRemoteDataSource(
                groupEventsResult = NetworkResult.NetworkError(IllegalStateException("offline")),
            ),
        )

        val result = repository.getGroupEvents(10L, YearMonth.of(2026, 8))

        assertTrue(result is MoilResult.Failure)
    }

    @Test
    fun `일정 생성 성공은 서버가 발급한 일정 식별자를 반환한다`() = runBlocking {
        val repository = EventRepositoryImpl(
            FakeEventRemoteDataSource(
                createEventResult = NetworkResult.Success(CreateEventResponseDto(99L)),
            ),
        )

        val result = repository.createEvent(groupEvent(), 10L, listOf(1L))

        assertEquals(MoilResult.Success(99L), result)
    }

    @Test
    fun `일정 수정과 삭제 성공은 성공 결과를 반환한다`() = runBlocking {
        val repository = EventRepositoryImpl(
            FakeEventRemoteDataSource(
                updateEventResult = NetworkResult.Success(Unit),
                deleteEventResult = NetworkResult.Success(Unit),
            ),
        )

        assertEquals(MoilResult.Success(Unit), repository.updateEvent(2L, groupEvent(), listOf(1L)))
        assertEquals(MoilResult.Success(Unit), repository.deleteEvent(2L))
    }

    private fun eventResponse(
        eventId: Long,
        title: String,
    ): EventResponseDto = EventResponseDto(
        eventId = eventId,
        title = title,
        startDate = "2026-08-03",
        endDate = "2026-08-05",
        isAllDay = true,
        members = listOf(EventMemberResponseDto(1L, "초록", "GREEN")),
    )

    private fun groupEvent(): GroupEvent = GroupEvent(
        id = 2L,
        title = "저녁 식사",
        startDate = "2026-08-03",
        endDate = "2026-08-05",
        isAllDay = true,
        startTime = null,
        endTime = null,
        location = null,
        members = emptyList(),
    )
}

private class FakeEventRemoteDataSource(
    private val groupEventsResult: NetworkResult<List<EventResponseDto>> = NetworkResult.Success(emptyList()),
    private val createEventResult: NetworkResult<CreateEventResponseDto> =
        NetworkResult.Success(CreateEventResponseDto(1L)),
    private val updateEventResult: NetworkResult<Unit> = NetworkResult.Success(Unit),
    private val deleteEventResult: NetworkResult<Unit> = NetworkResult.Success(Unit),
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
    ): NetworkResult<Unit> = updateEventResult

    override suspend fun deleteEvent(eventId: Long): NetworkResult<Unit> = deleteEventResult
}
