package com.example.moil.feature.event.module.data.repository

import com.example.moil.core.domain.MoilResult
import com.example.moil.core.network.NetworkResult
import com.example.moil.feature.event.module.data.dto.CreateEventResponseDto
import com.example.moil.feature.event.module.data.dto.EventAttendanceMemberResponseDto
import com.example.moil.feature.event.module.data.dto.EventAttendanceRequestStatus
import com.example.moil.feature.event.module.data.dto.EventAttendanceResponseDto
import com.example.moil.feature.event.module.data.dto.EventAttendanceUpdateResponseDto
import com.example.moil.feature.event.module.data.dto.EventAvailabilityCountResponseDto
import com.example.moil.feature.event.module.data.dto.EventAvailabilityResponseDto
import com.example.moil.feature.event.module.data.dto.EventAvailabilitySummaryResponseDto
import com.example.moil.feature.event.module.data.dto.EventAvailabilityTimeSlotDto
import com.example.moil.feature.event.module.data.dto.EventMemberResponseDto
import com.example.moil.feature.event.module.data.dto.EventRequestDto
import com.example.moil.feature.event.module.data.dto.EventResponseDto
import com.example.moil.feature.event.module.data.dto.MyEventAvailabilityResponseDto
import com.example.moil.feature.event.module.data.dto.UpdateEventAttendanceRequestDto
import com.example.moil.feature.event.module.data.dto.UpdateEventAvailabilityRequestDto
import com.example.moil.feature.event.module.data.dto.UpdateEventRequestDto
import com.example.moil.feature.event.module.data.remote.EventRemoteDataSource
import com.example.moil.feature.event.module.domain.model.EventAttendance
import com.example.moil.feature.event.module.domain.model.EventAttendanceChoice
import com.example.moil.feature.event.module.domain.model.EventAttendanceMember
import com.example.moil.feature.event.module.domain.model.EventAttendanceStatus
import com.example.moil.feature.event.module.domain.model.EventAttendanceUpdate
import com.example.moil.feature.event.module.domain.model.EventAvailabilityCount
import com.example.moil.feature.event.module.domain.model.EventAvailabilitySummary
import com.example.moil.feature.event.module.domain.model.EventAvailabilityTimeSlot
import com.example.moil.feature.event.module.domain.model.EventMember
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.group.module.domain.model.GroupColor
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
                        date = "2026-08-03",
                        startTime = null,
                        endTime = null,
                        location = null,
                        memo = null,
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
    fun `일정 상세 조회는 서버 메모를 포함한 Domain 일정으로 변환한다`() = runBlocking {
        val repository = EventRepositoryImpl(
            FakeEventRemoteDataSource(
                eventResult = NetworkResult.Success(
                    eventResponse(eventId = 3L, title = "상세 일정").copy(
                        memo = "준비물 확인",
                    ),
                ),
            ),
        )

        val result = repository.getEvent(3L)

        assertEquals("준비물 확인", (result as MoilResult.Success).value.memo)
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

    @Test
    fun `참석 현황 응답을 Domain 상태와 멤버로 변환한다`() = runBlocking {
        val repository = EventRepositoryImpl(
            FakeEventRemoteDataSource(
                attendanceResult = NetworkResult.Success(
                    EventAttendanceResponseDto(
                        myStatus = "ATTENDING",
                        participantCount = 2,
                        attendingCount = 1,
                        declinedCount = 0,
                        members = listOf(
                            EventAttendanceMemberResponseDto(
                                memberId = 11L,
                                nickname = "나",
                                colorId = "GREEN",
                                status = "ATTENDING",
                            ),
                            EventAttendanceMemberResponseDto(
                                memberId = 12L,
                                nickname = "가족",
                                colorId = "UNKNOWN_COLOR",
                                status = "PENDING",
                            ),
                        ),
                    ),
                ),
            ),
        )

        val result = repository.getEventAttendance(4L)

        assertEquals(
            MoilResult.Success(
                EventAttendance(
                    myStatus = EventAttendanceStatus.Attending,
                    participantCount = 2,
                    attendingCount = 1,
                    declinedCount = 0,
                    members = listOf(
                        EventAttendanceMember(11L, "나", GroupColor.Green, EventAttendanceStatus.Attending),
                        EventAttendanceMember(12L, "가족", GroupColor.Unknown, EventAttendanceStatus.Unknown),
                    ),
                ),
            ),
            result,
        )
    }

    @Test
    fun `참석 선택을 서버 코드로 직렬화 가능한 요청으로 변환한다`() = runBlocking {
        val remoteDataSource = FakeEventRemoteDataSource(
            attendanceUpdateResult = NetworkResult.Success(
                EventAttendanceUpdateResponseDto("ATTENDING", "2026-09-27T09:00:00Z"),
            ),
        )
        val repository = EventRepositoryImpl(remoteDataSource)

        val result = repository.updateEventAttendance(4L, EventAttendanceChoice.Attending)

        assertEquals(
            MoilResult.Success(
                EventAttendanceUpdate(EventAttendanceStatus.Attending, "2026-09-27T09:00:00Z"),
            ),
            result,
        )
        assertEquals(
            UpdateEventAttendanceRequestDto(EventAttendanceRequestStatus.Attending),
            remoteDataSource.lastAttendanceRequest,
        )
    }

    @Test
    fun `내 가능 시간대 응답이 null이면 null 성공 결과를 유지한다`() = runBlocking {
        val repository = EventRepositoryImpl(
            FakeEventRemoteDataSource(
                myAvailabilityResult = NetworkResult.Success(null),
            ),
        )

        assertEquals(MoilResult.Success(null), repository.getMyEventAvailability(4L, "2026-09-27"))
    }

    @Test
    fun `가능 시간대 저장 요청에 날짜와 시간 구간을 전달한다`() = runBlocking {
        val remoteDataSource = FakeEventRemoteDataSource()
        val repository = EventRepositoryImpl(remoteDataSource)
        val timeSlots = listOf(EventAvailabilityTimeSlot("09:30", "10:15"))

        val result = repository.updateMyEventAvailability(4L, "2026-09-27", timeSlots)

        assertEquals(MoilResult.Success(Unit), result)
        assertEquals(
            UpdateEventAvailabilityRequestDto(
                date = "2026-09-27",
                timeSlots = listOf(EventAvailabilityTimeSlotDto("09:30", "10:15")),
            ),
            remoteDataSource.lastAvailabilityRequest,
        )
    }

    @Test
    fun `가능 시간대 집계 응답에서 참여 수와 전원 가능 구간을 매핑한다`() = runBlocking {
        val repository = EventRepositoryImpl(
            FakeEventRemoteDataSource(
                availabilitySummaryResult = NetworkResult.Success(
                    EventAvailabilitySummaryResponseDto(
                        eventId = 4L,
                        date = "2026-09-27",
                        participantCount = 3,
                        respondedCount = 2,
                        timeSlots = listOf(
                            EventAvailabilityCountResponseDto(
                                startTime = "09:30",
                                endTime = "10:15",
                                availableCount = 3,
                                availableMemberIds = listOf(1L, 2L, 3L),
                                isAvailableForEveryone = true,
                            ),
                        ),
                    ),
                ),
            ),
        )

        val result = repository.getEventAvailabilitySummary(4L, "2026-09-27")

        assertEquals(
            MoilResult.Success(
                EventAvailabilitySummary(
                    eventId = 4L,
                    date = "2026-09-27",
                    participantCount = 3,
                    respondedCount = 2,
                    timeSlots = listOf(
                        EventAvailabilityCount("09:30", "10:15", 3, listOf(1L, 2L, 3L), true),
                    ),
                ),
            ),
            result,
        )
    }

    private fun eventResponse(
        eventId: Long,
        title: String,
    ): EventResponseDto = EventResponseDto(
        eventId = eventId,
        title = title,
        date = "2026-08-03",
        members = listOf(EventMemberResponseDto(1L, "초록", "GREEN")),
    )

    private fun groupEvent(): GroupEvent = GroupEvent(
        id = 2L,
        title = "저녁 식사",
        date = "2026-08-03",
        startTime = null,
        endTime = null,
        location = null,
        memo = null,
        members = emptyList(),
    )
}

private class FakeEventRemoteDataSource(
    private val groupEventsResult: NetworkResult<List<EventResponseDto>> = NetworkResult.Success(emptyList()),
    private val createEventResult: NetworkResult<CreateEventResponseDto> =
        NetworkResult.Success(CreateEventResponseDto(1L)),
    private val eventResult: NetworkResult<EventResponseDto> =
        NetworkResult.NetworkError(IllegalStateException("not used")),
    private val updateEventResult: NetworkResult<Unit> = NetworkResult.Success(Unit),
    private val deleteEventResult: NetworkResult<Unit> = NetworkResult.Success(Unit),
    private val attendanceResult: NetworkResult<EventAttendanceResponseDto> = NetworkResult.Success(
        EventAttendanceResponseDto(null, 0, 0, 0, emptyList()),
    ),
    private val attendanceUpdateResult: NetworkResult<EventAttendanceUpdateResponseDto> = NetworkResult.Success(
        EventAttendanceUpdateResponseDto("ATTENDING", "2026-09-27T09:00:00Z"),
    ),
    private val myAvailabilityResult: NetworkResult<MyEventAvailabilityResponseDto?> = NetworkResult.Success(null),
    private val availabilityResult: NetworkResult<EventAvailabilityResponseDto?> = NetworkResult.Success(null),
    private val availabilitySummaryResult: NetworkResult<EventAvailabilitySummaryResponseDto?> =
        NetworkResult.Success(null),
) : EventRemoteDataSource {

    var lastAttendanceRequest: UpdateEventAttendanceRequestDto? = null
        private set

    var lastAvailabilityRequest: UpdateEventAvailabilityRequestDto? = null
        private set

    override suspend fun getGroupEvents(
        groupId: Long,
        month: String,
    ): NetworkResult<List<EventResponseDto>> = groupEventsResult

    override suspend fun createEvent(
        request: EventRequestDto,
    ): NetworkResult<CreateEventResponseDto> = createEventResult

    override suspend fun getEvent(eventId: Long): NetworkResult<EventResponseDto> =
        eventResult

    override suspend fun updateEvent(
        eventId: Long,
        request: UpdateEventRequestDto,
    ): NetworkResult<Unit> = updateEventResult

    override suspend fun deleteEvent(eventId: Long): NetworkResult<Unit> = deleteEventResult

    override suspend fun getEventAttendance(eventId: Long): NetworkResult<EventAttendanceResponseDto> =
        attendanceResult

    override suspend fun updateEventAttendance(
        eventId: Long,
        request: UpdateEventAttendanceRequestDto,
    ): NetworkResult<EventAttendanceUpdateResponseDto> {
        lastAttendanceRequest = request
        return attendanceUpdateResult
    }

    override suspend fun deleteEventAttendance(eventId: Long): NetworkResult<Unit> = NetworkResult.Success(Unit)

    override suspend fun getMyEventAvailability(
        eventId: Long,
        date: String,
    ): NetworkResult<MyEventAvailabilityResponseDto?> = myAvailabilityResult

    override suspend fun getEventAvailability(
        eventId: Long,
        date: String,
    ): NetworkResult<EventAvailabilityResponseDto?> = availabilityResult

    override suspend fun getEventAvailabilitySummary(
        eventId: Long,
        date: String,
    ): NetworkResult<EventAvailabilitySummaryResponseDto?> = availabilitySummaryResult

    override suspend fun updateMyEventAvailability(
        eventId: Long,
        request: UpdateEventAvailabilityRequestDto,
    ): NetworkResult<Unit> {
        lastAvailabilityRequest = request
        return NetworkResult.Success(Unit)
    }

    override suspend fun deleteMyEventAvailability(eventId: Long, date: String): NetworkResult<Unit> =
        NetworkResult.Success(Unit)
}
