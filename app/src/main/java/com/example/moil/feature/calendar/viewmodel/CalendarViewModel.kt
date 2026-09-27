package com.example.moil.feature.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moil.core.domain.MoilError
import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.event.module.domain.model.EventAttendance
import com.example.moil.feature.event.module.domain.model.EventAttendanceChoice
import com.example.moil.feature.event.module.domain.model.EventAttendanceUpdate
import com.example.moil.feature.event.module.domain.model.EventAvailability
import com.example.moil.feature.event.module.domain.model.EventAvailabilitySummary
import com.example.moil.feature.event.module.domain.model.EventAvailabilityTimeSlot
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.event.module.domain.model.MyEventAvailability
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
import com.example.moil.feature.event.module.domain.usecase.UpdateMyEventAvailabilityUseCase
import com.example.moil.feature.event.module.domain.usecase.UpdateEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

sealed interface CalendarEffect {
    data object ScheduleCreated : CalendarEffect
    data class ScheduleUpdated(val eventId: Long) : CalendarEffect
    data object ScheduleDeleted : CalendarEffect

    /** 내 가능 시간을 저장했다. */
    data object AvailabilitySaved : CalendarEffect

    /** 내 가능 시간을 지웠다. */
    data object AvailabilityCleared : CalendarEffect
}

data class CalendarRemoteUiState(
    val selectedGroupId: Long? = null,
    val displayedMonth: YearMonth = YearMonth.now(),
    val events: List<GroupEvent> = emptyList(),
    val currentMonthEventCount: Int = 0,
    val selectedEvent: GroupEvent? = null,
    val isLoading: Boolean = false,
    val error: MoilError? = null,
    val isSelectedEventLoading: Boolean = false,
    val selectedEventError: MoilError? = null,
    val isMutationLoading: Boolean = false,
    val mutationError: MoilError? = null,
    // 참석·가능 시간 상태가 어느 일정(과 날짜)의 것인지 표시한다. 다른 일정의 늦은 응답은 이 키로 걸러낸다.
    val attendanceEventId: Long? = null,
    val attendance: CalendarRemoteLoadState<EventAttendance> = CalendarRemoteLoadState.Idle,
    val isAttendanceMutationLoading: Boolean = false,
    val attendanceMutationError: MoilError? = null,
    val availabilityEventId: Long? = null,
    val availabilityDate: String? = null,
    val myAvailability: CalendarRemoteLoadState<MyEventAvailability?> = CalendarRemoteLoadState.Idle,
    val memberAvailability: CalendarRemoteLoadState<EventAvailability?> = CalendarRemoteLoadState.Idle,
    val availabilitySummary: CalendarRemoteLoadState<EventAvailabilitySummary?> = CalendarRemoteLoadState.Idle,
    val isAvailabilityMutationLoading: Boolean = false,
    val availabilityMutationError: MoilError? = null,
)

sealed interface CalendarRemoteLoadState<out T> {
    data object Idle : CalendarRemoteLoadState<Nothing>
    data object Loading : CalendarRemoteLoadState<Nothing>
    data class Success<T>(val value: T) : CalendarRemoteLoadState<T>
    data class Failure(val error: MoilError) : CalendarRemoteLoadState<Nothing>
}

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getGroupEventsUseCase: GetGroupEventsUseCase,
    private val createEventUseCase: CreateEventUseCase,
    private val getEventUseCase: GetEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val getEventAttendanceUseCase: GetEventAttendanceUseCase,
    private val updateEventAttendanceUseCase: UpdateEventAttendanceUseCase,
    private val deleteEventAttendanceUseCase: DeleteEventAttendanceUseCase,
    private val getMyEventAvailabilityUseCase: GetMyEventAvailabilityUseCase,
    private val getEventAvailabilityUseCase: GetEventAvailabilityUseCase,
    private val getEventAvailabilitySummaryUseCase: GetEventAvailabilitySummaryUseCase,
    private val updateMyEventAvailabilityUseCase: UpdateMyEventAvailabilityUseCase,
    private val deleteMyEventAvailabilityUseCase: DeleteMyEventAvailabilityUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(CalendarRemoteUiState())
    val uiState: StateFlow<CalendarRemoteUiState> = mutableUiState.asStateFlow()

    private val mutableEffects = MutableSharedFlow<CalendarEffect>()
    val effects: SharedFlow<CalendarEffect> = mutableEffects.asSharedFlow()

    private var attendanceLoadJob: Job? = null
    private var availabilityLoadJob: Job? = null

    /** 그룹 선택 이벤트에서 서버의 표시 월 일정과 기기 현재 달 건수를 조회합니다. */
    fun selectGroup(groupId: Long) {
        mutableUiState.value = mutableUiState.value.copy(
            selectedGroupId = groupId,
            events = emptyList(),
            currentMonthEventCount = 0,
            selectedEvent = null,
            selectedEventError = null,
            error = null,
        )
        clearEventParticipationState()
        loadDisplayedMonthEvents()

        if (mutableUiState.value.displayedMonth != YearMonth.now()) {
            loadCurrentMonthEventCount()
        }
    }

    /** 월 이동 이벤트에서 서버의 표시 월 일정을 새로 조회합니다. */
    fun selectMonth(month: YearMonth) {
        mutableUiState.value = mutableUiState.value.copy(
            displayedMonth = month,
            events = emptyList(),
            error = null,
        )
        loadDisplayedMonthEvents()
    }

    /** 표시 월 조회 이벤트에서 서버 일정을 상태에 반영하고, 실패 시 목록을 비웁니다. */
    private fun loadDisplayedMonthEvents() = viewModelScope.launch {
        val state = mutableUiState.value
        val groupId = state.selectedGroupId ?: return@launch

        mutableUiState.value = state.copy(isLoading = true, error = null)

        when (val result = getGroupEventsUseCase(groupId, state.displayedMonth)) {
            is MoilResult.Success -> {
                mutableUiState.value = mutableUiState.value.copy(
                    events = result.value,
                    currentMonthEventCount = if (state.displayedMonth == YearMonth.now()) {
                        result.value.size
                    } else {
                        mutableUiState.value.currentMonthEventCount
                    },
                    isLoading = false,
                )
            }

            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(
                    events = emptyList(),
                    currentMonthEventCount = 0,
                    isLoading = false,
                    error = result.error,
                )
            }
        }
    }

    /** 일정 생성 요청을 실행하고 성공 시 목록을 새로 조회한 뒤 일회성 효과를 보냅니다. */
    fun createEvent(event: GroupEvent, sharedMemberIds: List<Long>) = viewModelScope.launch {
        val groupId = mutableUiState.value.selectedGroupId ?: return@launch
        mutableUiState.value = mutableUiState.value.copy(
            isMutationLoading = true,
            mutationError = null,
        )

        when (val result = createEventUseCase(event, groupId, sharedMemberIds)) {
            is MoilResult.Success -> {
                mutableUiState.value = mutableUiState.value.copy(isMutationLoading = false)
                refreshCalendarDataAfterMutation()
                mutableEffects.emit(CalendarEffect.ScheduleCreated)
            }

            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(
                    isMutationLoading = false,
                    mutationError = result.error,
                )
            }
        }
    }

    /** 일정 상세 조회 요청을 실행해 목록보다 최신인 서버 상세 데이터를 표시합니다. */
    fun loadEvent(eventId: Long) = viewModelScope.launch {
        if (mutableUiState.value.attendanceEventId != eventId) {
            clearEventParticipationState()
        }

        mutableUiState.value = mutableUiState.value.copy(
            selectedEvent = null,
            isSelectedEventLoading = true,
            selectedEventError = null,
        )

        when (val result = getEventUseCase(eventId)) {
            is MoilResult.Success -> {
                mutableUiState.value = mutableUiState.value.copy(
                    selectedEvent = result.value,
                    isSelectedEventLoading = false,
                )
                loadEventAttendance(eventId)
            }

            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(
                    isSelectedEventLoading = false,
                    selectedEventError = result.error,
                )
            }
        }

    }

    /** 일정 상세 진입 시 참석 현황을 서버에서 조회합니다. */
    fun loadEventAttendance(eventId: Long) {
        attendanceLoadJob?.cancel()
        attendanceLoadJob = viewModelScope.launch {
            mutableUiState.value = mutableUiState.value.copy(
                attendanceEventId = eventId,
                attendance = CalendarRemoteLoadState.Loading,
            )

            val attendanceResult = getEventAttendanceUseCase(eventId).toCalendarRemoteLoadState()

            if (mutableUiState.value.attendanceEventId == eventId) {
                mutableUiState.value = mutableUiState.value.copy(attendance = attendanceResult)
            }
        }
    }

    /** 사용자가 참석 또는 불참을 선택하면 저장한 뒤 최신 참석 현황을 다시 조회합니다. */
    fun updateEventAttendance(eventId: Long, choice: EventAttendanceChoice) = viewModelScope.launch {
        if (mutableUiState.value.isAttendanceMutationLoading) {
            return@launch
        }

        mutableUiState.value = mutableUiState.value.copy(
            isAttendanceMutationLoading = true,
            attendanceMutationError = null,
        )

        when (val result = updateEventAttendanceUseCase(eventId, choice)) {
            is MoilResult.Success -> {
                mutableUiState.value = mutableUiState.value.copy(isAttendanceMutationLoading = false)

                // 요청 사이 다른 일정으로 옮겼다면 이전 일정 현황을 다시 불러오지 않는다.
                if (mutableUiState.value.attendanceEventId == eventId) {
                    loadEventAttendance(eventId)
                }
            }

            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(
                    isAttendanceMutationLoading = false,
                    attendanceMutationError = result.error,
                )
            }
        }
    }

    /** 현재 참석 응답을 취소하고 성공 시 참석 현황을 새로 조회합니다. */
    fun deleteEventAttendance(eventId: Long) = viewModelScope.launch {
        if (mutableUiState.value.isAttendanceMutationLoading) {
            return@launch
        }

        mutableUiState.value = mutableUiState.value.copy(
            isAttendanceMutationLoading = true,
            attendanceMutationError = null,
        )

        when (val result = deleteEventAttendanceUseCase(eventId)) {
            is MoilResult.Success -> {
                mutableUiState.value = mutableUiState.value.copy(isAttendanceMutationLoading = false)

                // 요청 사이 다른 일정으로 옮겼다면 이전 일정 현황을 다시 불러오지 않는다.
                if (mutableUiState.value.attendanceEventId == eventId) {
                    loadEventAttendance(eventId)
                }
            }

            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(
                    isAttendanceMutationLoading = false,
                    attendanceMutationError = result.error,
                )
            }
        }
    }

    /** 일정 날짜의 내 응답, 멤버별 현황과 시간대 집계를 병렬 조회합니다. */
    fun loadEventAvailability(eventId: Long, date: String) {
        availabilityLoadJob?.cancel()
        availabilityLoadJob = viewModelScope.launch {
            mutableUiState.value = mutableUiState.value.copy(
                availabilityEventId = eventId,
                availabilityDate = date,
                myAvailability = CalendarRemoteLoadState.Loading,
                memberAvailability = CalendarRemoteLoadState.Loading,
                availabilitySummary = CalendarRemoteLoadState.Loading,
            )

            coroutineScope {
                val myAvailabilityResult = async {
                    getMyEventAvailabilityUseCase(eventId, date)
                }
                val memberAvailabilityResult = async {
                    getEventAvailabilityUseCase(eventId, date)
                }
                val availabilitySummaryResult = async {
                    getEventAvailabilitySummaryUseCase(eventId, date)
                }

                val myAvailabilityState = myAvailabilityResult.await().toCalendarRemoteLoadState()
                val memberAvailabilityState = memberAvailabilityResult.await().toCalendarRemoteLoadState()
                val availabilitySummaryState = availabilitySummaryResult.await().toCalendarRemoteLoadState()
                val latestState = mutableUiState.value

                if (latestState.availabilityEventId == eventId && latestState.availabilityDate == date) {
                    mutableUiState.value = latestState.copy(
                        myAvailability = myAvailabilityState,
                        memberAvailability = memberAvailabilityState,
                        availabilitySummary = availabilitySummaryState,
                    )
                }
            }
        }
    }

    /** 사용자가 편집한 가능 시간대를 저장하고 성공 시 해당 날짜의 현황을 갱신합니다. */
    fun updateMyEventAvailability(
        eventId: Long,
        date: String,
        timeSlots: List<EventAvailabilityTimeSlot>,
    ) = viewModelScope.launch {
        if (mutableUiState.value.isAvailabilityMutationLoading) {
            return@launch
        }

        mutableUiState.value = mutableUiState.value.copy(
            isAvailabilityMutationLoading = true,
            availabilityMutationError = null,
        )

        when (val result = updateMyEventAvailabilityUseCase(eventId, date, timeSlots)) {
            is MoilResult.Success -> {
                mutableUiState.value = mutableUiState.value.copy(isAvailabilityMutationLoading = false)
                mutableEffects.emit(CalendarEffect.AvailabilitySaved)

                if (mutableUiState.value.availabilityEventId == eventId) {
                    loadEventAvailability(eventId, date)
                }
            }

            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(
                    isAvailabilityMutationLoading = false,
                    availabilityMutationError = result.error,
                )
            }
        }
    }

    /** 사용자의 해당 날짜 가능 시간대를 삭제하고 성공 시 최신 현황을 조회합니다. */
    fun deleteMyEventAvailability(eventId: Long, date: String) = viewModelScope.launch {
        if (mutableUiState.value.isAvailabilityMutationLoading) {
            return@launch
        }

        mutableUiState.value = mutableUiState.value.copy(
            isAvailabilityMutationLoading = true,
            availabilityMutationError = null,
        )

        when (val result = deleteMyEventAvailabilityUseCase(eventId, date)) {
            is MoilResult.Success -> {
                mutableUiState.value = mutableUiState.value.copy(isAvailabilityMutationLoading = false)
                mutableEffects.emit(CalendarEffect.AvailabilityCleared)

                if (mutableUiState.value.availabilityEventId == eventId) {
                    loadEventAvailability(eventId, date)
                }
            }

            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(
                    isAvailabilityMutationLoading = false,
                    availabilityMutationError = result.error,
                )
            }
        }
    }

    /** 일정 수정 요청을 실행하고 성공 시 상세 데이터를 다시 조회합니다. */
    fun updateEvent(
        eventId: Long,
        event: GroupEvent,
        sharedMemberIds: List<Long>,
    ) = viewModelScope.launch {
        mutableUiState.value = mutableUiState.value.copy(
            isMutationLoading = true,
            mutationError = null,
        )

        when (val result = updateEventUseCase(eventId, event, sharedMemberIds)) {
            is MoilResult.Success -> {
                mutableUiState.value = mutableUiState.value.copy(isMutationLoading = false)
                refreshCalendarDataAfterMutation()
                mutableEffects.emit(CalendarEffect.ScheduleUpdated(eventId))
            }

            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(
                    isMutationLoading = false,
                    mutationError = result.error,
                )
            }
        }
    }

    /** 일정 삭제 요청을 실행하고 성공 시 목록을 새로 조회한 뒤 시트를 닫습니다. */
    fun deleteEvent(eventId: Long) = viewModelScope.launch {
        mutableUiState.value = mutableUiState.value.copy(
            isMutationLoading = true,
            mutationError = null,
        )

        when (val result = deleteEventUseCase(eventId)) {
            is MoilResult.Success -> {
                mutableUiState.value = mutableUiState.value.copy(isMutationLoading = false)
                refreshCalendarDataAfterMutation()
                mutableEffects.emit(CalendarEffect.ScheduleDeleted)
            }

            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(
                    isMutationLoading = false,
                    mutationError = result.error,
                )
            }
        }
    }

    /** 기기 현재 달 일정 수 조회가 실패하면 가족 화면의 건수를 0으로 초기화합니다. */
    // 그룹이나 일정이 바뀌면 이전 일정의 참석·가능 시간 상태와 진행 중인 조회를 버린다.
    private fun clearEventParticipationState() {
        attendanceLoadJob?.cancel()
        availabilityLoadJob?.cancel()
        mutableUiState.value = mutableUiState.value.copy(
            attendanceEventId = null,
            attendance = CalendarRemoteLoadState.Idle,
            attendanceMutationError = null,
            availabilityEventId = null,
            availabilityDate = null,
            myAvailability = CalendarRemoteLoadState.Idle,
            memberAvailability = CalendarRemoteLoadState.Idle,
            availabilitySummary = CalendarRemoteLoadState.Idle,
            availabilityMutationError = null,
        )
    }

    private fun loadCurrentMonthEventCount() = viewModelScope.launch {
        val groupId = mutableUiState.value.selectedGroupId ?: return@launch
        val currentMonth = YearMonth.now()

        when (val result = getGroupEventsUseCase(groupId, currentMonth)) {
            is MoilResult.Success -> {
                mutableUiState.value = mutableUiState.value.copy(
                    currentMonthEventCount = result.value.size,
                )
            }

            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(
                    currentMonthEventCount = 0,
                    error = result.error,
                )
            }
        }
    }

    private fun refreshCalendarDataAfterMutation() {
        loadDisplayedMonthEvents()

        if (mutableUiState.value.displayedMonth != YearMonth.now()) {
            loadCurrentMonthEventCount()
        }
    }
}

private fun <T> MoilResult<T>.toCalendarRemoteLoadState(): CalendarRemoteLoadState<T> = when (this) {
    is MoilResult.Success -> CalendarRemoteLoadState.Success(value)
    is MoilResult.Failure -> CalendarRemoteLoadState.Failure(error)
}
