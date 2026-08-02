package com.example.moil.feature.calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moil.core.domain.MoilError
import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.event.domain.GetGroupEventsUseCase
import com.example.moil.feature.event.domain.CreateEventUseCase
import com.example.moil.feature.event.domain.GetEventUseCase
import com.example.moil.feature.event.domain.UpdateEventUseCase
import com.example.moil.feature.event.domain.DeleteEventUseCase
import com.example.moil.feature.event.domain.GroupEvent
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

    /** 그룹 전환 이벤트에서 선택 그룹과 현재 월의 일정을 조회합니다. */
    fun selectGroup(groupId: Long) {
        mutableUiState.value = mutableUiState.value.copy(selectedGroupId = groupId, events = emptyList(), error = null)
        loadEvents()
    }

    /** 월 이동 이벤트에서 YYYY-MM 형식으로 일정 조회 UseCase를 호출합니다. */
    fun selectMonth(month: YearMonth) {
        mutableUiState.value = mutableUiState.value.copy(displayedMonth = month, error = null)
        loadEvents()
    }

    fun loadEvents() = viewModelScope.launch {
        val state = mutableUiState.value
        val groupId = state.selectedGroupId ?: return@launch
        mutableUiState.value = state.copy(isLoading = true, error = null)
        when (val result = getGroupEventsUseCase(groupId, state.displayedMonth.toString())) {
            is MoilResult.Success -> mutableUiState.value = mutableUiState.value.copy(isLoading = false, events = result.value)
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(isLoading = false, error = result.error)
        }
    }

    fun createEvent(event: GroupEvent, sharedMemberIds: List<Long>) = viewModelScope.launch {
        val groupId = mutableUiState.value.selectedGroupId ?: return@launch
        when (val result = createEventUseCase(event, groupId, sharedMemberIds)) {
            is MoilResult.Success -> loadEvents()
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(error = result.error)
        }
    }

    fun loadEvent(eventId: Long) = viewModelScope.launch {
        when (val result = getEventUseCase(eventId)) {
            is MoilResult.Success -> mutableUiState.value = mutableUiState.value.copy(selectedEvent = result.value)
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(error = result.error)
        }
    }

    fun updateEvent(eventId: Long, event: GroupEvent, sharedMemberIds: List<Long>) = viewModelScope.launch {
        when (val result = updateEventUseCase(eventId, event, sharedMemberIds)) {
            is MoilResult.Success -> loadEvents()
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(error = result.error)
        }
    }

    fun deleteEvent(eventId: Long) = viewModelScope.launch {
        when (val result = deleteEventUseCase(eventId)) {
            is MoilResult.Success -> loadEvents()
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(error = result.error)
        }
    }
}
