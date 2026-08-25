package com.example.moil.feature.calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moil.core.domain.MoilError
import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.event.module.domain.usecase.CreateEventUseCase
import com.example.moil.feature.event.module.domain.usecase.DeleteEventUseCase
import com.example.moil.feature.event.module.domain.usecase.GetEventUseCase
import com.example.moil.feature.event.module.domain.usecase.GetGroupEventsUseCase
import com.example.moil.feature.event.module.domain.usecase.UpdateEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import javax.inject.Inject
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
    private val getGroupEventsUseCase: GetGroupEventsUseCase,
    private val createEventUseCase: CreateEventUseCase,
    private val getEventUseCase: GetEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(CalendarRemoteUiState())
    val uiState: StateFlow<CalendarRemoteUiState> = mutableUiState.asStateFlow()

    /** 그룹 선택 이벤트에서 서버의 표시 월 일정과 기기 현재 달 건수를 조회합니다. */
    fun selectGroup(groupId: Long) {
        mutableUiState.value = mutableUiState.value.copy(
            selectedGroupId = groupId,
            events = emptyList(),
            currentMonthEventCount = 0,
            error = null,
        )
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

    /** 일정 저장 이벤트에서 서버 생성 성공 후 표시 월과 현재 달 정보를 다시 조회합니다. */
    fun createEvent(event: GroupEvent, sharedMemberIds: List<Long>) = viewModelScope.launch {
        val groupId = mutableUiState.value.selectedGroupId ?: return@launch

        when (val result = createEventUseCase(event, groupId, sharedMemberIds)) {
            is MoilResult.Success -> refreshCalendarDataAfterMutation()
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

    /** 일정 수정 이벤트에서 서버 성공 후 표시 월과 현재 달 정보를 다시 조회합니다. */
    fun updateEvent(
        eventId: Long,
        event: GroupEvent,
        sharedMemberIds: List<Long>,
    ) = viewModelScope.launch {
        when (val result = updateEventUseCase(eventId, event, sharedMemberIds)) {
            is MoilResult.Success -> refreshCalendarDataAfterMutation()
            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(error = result.error)
            }
        }
    }

    /** 일정 삭제 이벤트에서 서버 성공 후 표시 월과 현재 달 정보를 다시 조회합니다. */
    fun deleteEvent(eventId: Long) = viewModelScope.launch {
        when (val result = deleteEventUseCase(eventId)) {
            is MoilResult.Success -> refreshCalendarDataAfterMutation()
            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(error = result.error)
            }
        }
    }

    /** 기기 현재 달 일정 수 조회가 실패하면 가족 화면의 건수를 0으로 초기화합니다. */
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
