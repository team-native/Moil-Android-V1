package com.example.moil.feature.calendar.viewmodel

import com.example.moil.core.domain.MoilError
import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.event.module.domain.model.EventAttendance
import com.example.moil.feature.event.module.domain.model.EventAttendanceChoice
import com.example.moil.feature.event.module.domain.model.EventAttendanceStatus
import com.example.moil.feature.event.module.domain.model.EventAttendanceUpdate
import com.example.moil.feature.event.module.domain.model.EventAvailability
import com.example.moil.feature.event.module.domain.model.EventAvailabilitySummary
import com.example.moil.feature.event.module.domain.model.EventAvailabilityTimeSlot
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.event.module.domain.model.MyEventAvailability
import com.example.moil.feature.event.module.domain.repository.EventRepository
import com.example.moil.feature.event.module.domain.usecase.CreateEventUseCase
import com.example.moil.feature.event.module.domain.usecase.DeleteEventAttendanceUseCase
import com.example.moil.feature.event.module.domain.usecase.DeleteEventUseCase
import com.example.moil.feature.event.module.domain.usecase.DeleteMyEventAvailabilityUseCase
import com.example.moil.feature.event.module.domain.usecase.GetEventAttendanceUseCase
import com.example.moil.feature.event.module.domain.usecase.GetEventAvailabilitySummaryUseCase
import com.example.moil.feature.event.module.domain.usecase.GetEventAvailabilityUseCase
import com.example.moil.feature.event.module.domain.usecase.GetEventUseCase
import com.example.moil.feature.event.module.domain.usecase.GetGroupEventsUseCase
import com.example.moil.feature.event.module.domain.usecase.GetMyEventAvailabilityUseCase
import com.example.moil.feature.event.module.domain.usecase.UpdateEventAttendanceUseCase
import com.example.moil.feature.event.module.domain.usecase.UpdateEventUseCase
import com.example.moil.feature.event.module.domain.usecase.UpdateMyEventAvailabilityUseCase
import java.time.YearMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

private const val EVENT_ID = 42L
private const val OTHER_EVENT_ID = 43L
private const val EVENT_DATE = "2026-09-27"

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelRemoteStateTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `일정 상세 조회 성공 후 참석 현황을 조회한다`() = runTest {
        val repository = FakeEventRepository()
        val viewModel = createCalendarViewModel(repository)

        viewModel.loadEvent(EVENT_ID)
        advanceUntilIdle()

        assertEquals(EVENT_ID, viewModel.uiState.value.selectedEvent?.id)
        assertEquals(listOf(EVENT_ID), repository.attendanceRequests)
        assertEquals(
            CalendarRemoteLoadState.Success(repository.attendance),
            viewModel.uiState.value.attendance,
        )
    }

    @Test
    fun `참석 응답 저장 성공 후 현황을 다시 조회하고 로딩을 종료한다`() = runTest {
        val repository = FakeEventRepository()
        val viewModel = createCalendarViewModel(repository)

        // 상세 화면에서 참석 현황을 본 상태에서 응답하는 실제 흐름을 따른다.
        viewModel.loadEventAttendance(EVENT_ID)
        advanceUntilIdle()
        viewModel.updateEventAttendance(EVENT_ID, EventAttendanceChoice.Attending)
        advanceUntilIdle()

        assertEquals(listOf(EVENT_ID to EventAttendanceChoice.Attending), repository.attendanceUpdates)
        assertEquals(listOf(EVENT_ID, EVENT_ID), repository.attendanceRequests)
        assertEquals(
            CalendarRemoteLoadState.Success(repository.attendance),
            viewModel.uiState.value.attendance,
        )
        assertFalse(viewModel.uiState.value.isAttendanceMutationLoading)
        assertNull(viewModel.uiState.value.attendanceMutationError)
    }

    @Test
    fun `다른 일정으로 옮긴 뒤 끝난 이전 일정의 응답 저장은 현황을 다시 조회하지 않는다`() = runTest {
        val repository = FakeEventRepository()
        val viewModel = createCalendarViewModel(repository)

        viewModel.loadEventAttendance(OTHER_EVENT_ID)
        advanceUntilIdle()
        viewModel.updateEventAttendance(EVENT_ID, EventAttendanceChoice.Declined)
        advanceUntilIdle()

        assertEquals(listOf(OTHER_EVENT_ID), repository.attendanceRequests)
        assertEquals(OTHER_EVENT_ID, viewModel.uiState.value.attendanceEventId)
    }

    @Test
    fun `가능 시간대 조회는 같은 날짜로 세 서버 조회를 모두 실행한다`() = runTest {
        val repository = FakeEventRepository()
        val viewModel = createCalendarViewModel(repository)

        viewModel.loadEventAvailability(EVENT_ID, EVENT_DATE)
        advanceUntilIdle()

        assertEquals(listOf(EVENT_ID to EVENT_DATE), repository.myAvailabilityRequests)
        assertEquals(listOf(EVENT_ID to EVENT_DATE), repository.memberAvailabilityRequests)
        assertEquals(listOf(EVENT_ID to EVENT_DATE), repository.availabilitySummaryRequests)
        assertEquals(
            CalendarRemoteLoadState.Success(repository.myAvailability),
            viewModel.uiState.value.myAvailability,
        )
        assertEquals(
            CalendarRemoteLoadState.Success(repository.memberAvailability),
            viewModel.uiState.value.memberAvailability,
        )
        assertEquals(
            CalendarRemoteLoadState.Success(repository.availabilitySummary),
            viewModel.uiState.value.availabilitySummary,
        )
    }

    private fun createCalendarViewModel(repository: EventRepository): CalendarViewModel = CalendarViewModel(
        getGroupEventsUseCase = GetGroupEventsUseCase(repository),
        createEventUseCase = CreateEventUseCase(repository),
        getEventUseCase = GetEventUseCase(repository),
        updateEventUseCase = UpdateEventUseCase(repository),
        deleteEventUseCase = DeleteEventUseCase(repository),
        getEventAttendanceUseCase = GetEventAttendanceUseCase(repository),
        updateEventAttendanceUseCase = UpdateEventAttendanceUseCase(repository),
        deleteEventAttendanceUseCase = DeleteEventAttendanceUseCase(repository),
        getMyEventAvailabilityUseCase = GetMyEventAvailabilityUseCase(repository),
        getEventAvailabilityUseCase = GetEventAvailabilityUseCase(repository),
        getEventAvailabilitySummaryUseCase = GetEventAvailabilitySummaryUseCase(repository),
        updateMyEventAvailabilityUseCase = UpdateMyEventAvailabilityUseCase(repository),
        deleteMyEventAvailabilityUseCase = DeleteMyEventAvailabilityUseCase(repository),
    )
}

private class FakeEventRepository : EventRepository {
    val attendance = EventAttendance(
        myStatus = EventAttendanceStatus.Attending,
        participantCount = 2,
        attendingCount = 1,
        declinedCount = 0,
        members = emptyList(),
    )
    val myAvailability: MyEventAvailability? = null
    val memberAvailability: EventAvailability? = null
    val availabilitySummary: EventAvailabilitySummary? = null
    val attendanceRequests = mutableListOf<Long>()
    val attendanceUpdates = mutableListOf<Pair<Long, EventAttendanceChoice>>()
    val myAvailabilityRequests = mutableListOf<Pair<Long, String>>()
    val memberAvailabilityRequests = mutableListOf<Pair<Long, String>>()
    val availabilitySummaryRequests = mutableListOf<Pair<Long, String>>()

    override suspend fun getGroupEvents(
        groupId: Long,
        month: YearMonth,
    ): MoilResult<List<GroupEvent>> = unused()

    override suspend fun createEvent(
        event: GroupEvent,
        groupId: Long,
        memberIds: List<Long>,
    ): MoilResult<Long> = unused()

    override suspend fun getEvent(eventId: Long): MoilResult<GroupEvent> = MoilResult.Success(
        GroupEvent(
            id = eventId,
            title = "가족 모임",
            date = EVENT_DATE,
            startTime = "12:00",
            endTime = "13:00",
            location = null,
            memo = null,
            members = emptyList(),
        ),
    )

    override suspend fun updateEvent(
        eventId: Long,
        event: GroupEvent,
        memberIds: List<Long>,
    ): MoilResult<Unit> = unused()

    override suspend fun deleteEvent(eventId: Long): MoilResult<Unit> = unused()

    override suspend fun getEventAttendance(eventId: Long): MoilResult<EventAttendance> {
        attendanceRequests += eventId
        return MoilResult.Success(attendance)
    }

    override suspend fun updateEventAttendance(
        eventId: Long,
        choice: EventAttendanceChoice,
    ): MoilResult<EventAttendanceUpdate> {
        attendanceUpdates += eventId to choice
        return MoilResult.Success(
            EventAttendanceUpdate(
                status = EventAttendanceStatus.Attending,
                updatedAt = "2026-09-27T09:00:00Z",
            ),
        )
    }

    override suspend fun deleteEventAttendance(eventId: Long): MoilResult<Unit> = unused()

    override suspend fun getMyEventAvailability(
        eventId: Long,
        date: String,
    ): MoilResult<MyEventAvailability?> {
        myAvailabilityRequests += eventId to date
        return MoilResult.Success(myAvailability)
    }

    override suspend fun getEventAvailability(
        eventId: Long,
        date: String,
    ): MoilResult<EventAvailability?> {
        memberAvailabilityRequests += eventId to date
        return MoilResult.Success(memberAvailability)
    }

    override suspend fun getEventAvailabilitySummary(
        eventId: Long,
        date: String,
    ): MoilResult<EventAvailabilitySummary?> {
        availabilitySummaryRequests += eventId to date
        return MoilResult.Success(availabilitySummary)
    }

    override suspend fun updateMyEventAvailability(
        eventId: Long,
        date: String,
        timeSlots: List<EventAvailabilityTimeSlot>,
    ): MoilResult<Unit> = unused()

    override suspend fun deleteMyEventAvailability(eventId: Long, date: String): MoilResult<Unit> = unused()

    private fun <T> unused(): MoilResult<T> = MoilResult.Failure(
        MoilError.Configuration("사용하지 않는 FakeEventRepository 메소드입니다."),
    )
}
