package com.example.moil.feature.calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moil.core.domain.MoilError
import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.event.domain.CreateEventUseCase
import com.example.moil.feature.event.domain.DeleteEventUseCase
import com.example.moil.feature.event.domain.GetEventUseCase
import com.example.moil.feature.event.domain.GroupEvent
import com.example.moil.feature.event.domain.ObserveGroupEventsUseCase
import com.example.moil.feature.event.domain.ObserveMonthlyEventCountUseCase
import com.example.moil.feature.event.domain.RefreshGroupEventsUseCase
import com.example.moil.feature.event.domain.UpdateEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CalendarRemoteUiState(
    val selectedGroupId: Long? = null,
    val displayedMonth: YearMonth = YearMonth.now(),
    val events: List<GroupEvent> = emptyList(),
    val currentMonthEventCount: Int = 0,
    val selectedEvent: GroupEvent? = null,
    val isLoading: Boolean = false,
    val error: MoilError? = null,
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val observeGroupEventsUseCase: ObserveGroupEventsUseCase,
    private val observeMonthlyEventCountUseCase: ObserveMonthlyEventCountUseCase,
    private val refreshGroupEventsUseCase: RefreshGroupEventsUseCase,
    private val createEventUseCase: CreateEventUseCase,
    private val getEventUseCase: GetEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(CalendarRemoteUiState())
    val uiState: StateFlow<CalendarRemoteUiState> = mutableUiState.asStateFlow()

    private var eventsObservationJob: Job? = null
    private var currentMonthCountObservationJob: Job? = null

    /** 그룹 선택 이벤트에서 해당 그룹의 Room 일정과 기기 현재 달 건수를 구독하고 서버 캐시를 갱신합니다. */
    fun selectGroup(groupId: Long) {
        mutableUiState.value = mutableUiState.value.copy(
            selectedGroupId = groupId,
            events = emptyList(),
            currentMonthEventCount = 0,
            error = null,
        )
        observeDisplayedMonthEvents()
        observeCurrentMonthEventCount()
        refreshEvents()
    }

    /** 월 이동 이벤트에서 표시 월의 Room 일정 구독을 교체하고 서버 캐시를 갱신합니다. */
    fun selectMonth(month: YearMonth) {
        mutableUiState.value = mutableUiState.value.copy(
            displayedMonth = month,
            error = null,
        )
        observeDisplayedMonthEvents()
        refreshEvents()
    }

    /** 새로고침 이벤트에서 서버 월별 일정을 받아 Room 캐시를 교체하고, 실패 시 기존 캐시는 유지합니다. */
    fun refreshEvents() = viewModelScope.launch {
        val state = mutableUiState.value
        val groupId = state.selectedGroupId ?: return@launch

        mutableUiState.value = state.copy(isLoading = true, error = null)

        when (val result = refreshGroupEventsUseCase(groupId, state.displayedMonth)) {
            is MoilResult.Success -> {
                mutableUiState.value = mutableUiState.value.copy(isLoading = false)
            }

            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(
                    isLoading = false,
                    error = result.error,
                )
            }
        }
    }

    /** 일정 저장 이벤트에서 서버 생성 성공 후 Repository가 Room에 저장하도록 요청합니다. */
    fun createEvent(event: GroupEvent, sharedMemberIds: List<Long>) = viewModelScope.launch {
        val groupId = mutableUiState.value.selectedGroupId ?: return@launch

        when (val result = createEventUseCase(event, groupId, sharedMemberIds)) {
            is MoilResult.Success -> Unit
            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(error = result.error)
            }
        }
    }

    fun loadEvent(eventId: Long) = viewModelScope.launch {
        when (val result = getEventUseCase(eventId)) {
            is MoilResult.Success -> {
                mutableUiState.value = mutableUiState.value.copy(selectedEvent = result.value)
            }

            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(error = result.error)
            }
        }
    }

    /** 일정 수정 이벤트에서 서버 성공 후 Repository가 Room 캐시를 해당 일정으로 갱신합니다. */
    fun updateEvent(
        eventId: Long,
        event: GroupEvent,
        sharedMemberIds: List<Long>,
    ) = viewModelScope.launch {
        when (val result = updateEventUseCase(eventId, event, sharedMemberIds)) {
            is MoilResult.Success -> Unit
            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(error = result.error)
            }
        }
    }

    /** 일정 삭제 이벤트에서 서버 성공 후 Repository가 Room 캐시에서도 삭제합니다. */
    fun deleteEvent(eventId: Long) = viewModelScope.launch {
        when (val result = deleteEventUseCase(eventId)) {
            is MoilResult.Success -> Unit
            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(error = result.error)
            }
        }
    }

    private fun observeDisplayedMonthEvents() {
        eventsObservationJob?.cancel()

        val state = mutableUiState.value
        val groupId = state.selectedGroupId ?: return

        eventsObservationJob = viewModelScope.launch {
            observeGroupEventsUseCase(groupId, state.displayedMonth).collect { events ->
                mutableUiState.value = mutableUiState.value.copy(events = events)
            }
        }
    }

    private fun observeCurrentMonthEventCount() {
        currentMonthCountObservationJob?.cancel()

        val groupId = mutableUiState.value.selectedGroupId ?: return
        val currentMonth = YearMonth.now()

        currentMonthCountObservationJob = viewModelScope.launch {
            observeMonthlyEventCountUseCase(groupId, currentMonth).collect { eventCount ->
                mutableUiState.value = mutableUiState.value.copy(
                    currentMonthEventCount = eventCount,
                )
            }
        }
    }
}
