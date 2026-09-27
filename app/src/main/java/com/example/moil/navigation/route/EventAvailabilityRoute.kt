package com.example.moil.navigation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moil.R
import com.example.moil.core.component.MoilConfirmDialog
import com.example.moil.core.component.MoilConfirmDialogTone
import com.example.moil.feature.calendar.view.EventAvailabilityScreen
import com.example.moil.feature.calendar.viewmodel.CalendarViewModel
import com.example.moil.feature.calendar.viewmodel.EventAvailabilityDisplayModel
import com.example.moil.feature.calendar.viewmodel.EventAvailabilityScreenEvent
import com.example.moil.feature.calendar.viewmodel.EventAvailabilityTab
import com.example.moil.feature.calendar.viewmodel.EventSectionLoadState
import com.example.moil.feature.calendar.viewmodel.toAvailabilityTimeSlots
import com.example.moil.feature.calendar.viewmodel.toEventAvailabilityDisplayModel
import com.example.moil.navigation.MoilMainDestination
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState

/**
 * 일정 상세에서 여는 가능 시간 화면 목적지다.
 *
 * 대상 일정·날짜와 사용자가 고른 칸은 [MoilMainUiState.eventAvailabilityUiState]가 보관하고,
 * 서버 응답은 [CalendarViewModel]에서 받아 표시 모델로 바꾼다.
 */
@Composable
internal fun EventAvailabilityRoute(
    mainUiState: MoilMainUiState,
    calendarViewModel: CalendarViewModel,
    navigator: MoilMainNavigator,
) {
    val calendarRemoteUiState by calendarViewModel.uiState.collectAsStateWithLifecycle()
    val availabilityUiState = mainUiState.eventAvailabilityUiState
    val eventId = availabilityUiState.eventId
    val eventDate = availabilityUiState.date
    val displayModel = if (eventId != null && eventDate != null) {
        calendarRemoteUiState.toEventAvailabilityDisplayModel(
            eventId = eventId,
            date = eventDate.toString(),
        )
    } else {
        EventAvailabilityDisplayModel()
    }

    // 서버의 내 응답이 처음 도착하면 한 번만 내 선택으로 채우고,
    // 아직 입력하지 않았다면 바로 입력할 수 있게 "내 시간 입력" 탭으로 시작한다.
    LaunchedEffect(displayModel.loadState, displayModel.mySlotStarts) {
        val latestUiState = mainUiState.eventAvailabilityUiState

        if (displayModel.loadState == EventSectionLoadState.Loaded && !latestUiState.isMySelectionInitialized) {
            mainUiState.eventAvailabilityUiState = latestUiState.copy(
                selectedSlotStarts = displayModel.mySlotStarts,
                isMySelectionInitialized = true,
                selectedTab = if (displayModel.hasMyAvailability) {
                    EventAvailabilityTab.Together
                } else {
                    EventAvailabilityTab.Mine
                },
            )
        }
    }

    EventAvailabilityScreen(
        uiState = availabilityUiState,
        displayModel = displayModel,
        onEvent = { event ->
            when (event) {
                EventAvailabilityScreenEvent.BackClicked -> navigator.goBack()

                is EventAvailabilityScreenEvent.TabSelected -> {
                    mainUiState.eventAvailabilityUiState = mainUiState.eventAvailabilityUiState.copy(
                        selectedTab = event.tab,
                        focusedSlotStart = null,
                    )
                }

                is EventAvailabilityScreenEvent.SlotClicked -> {
                    val latestUiState = mainUiState.eventAvailabilityUiState

                    mainUiState.eventAvailabilityUiState = when (latestUiState.selectedTab) {
                        EventAvailabilityTab.Together -> latestUiState.copy(
                            focusedSlotStart = event.slotStart.takeIf { slotStart ->
                                slotStart != latestUiState.focusedSlotStart
                            },
                        )

                        EventAvailabilityTab.Mine -> latestUiState.copy(
                            selectedSlotStarts = if (event.slotStart in latestUiState.selectedSlotStarts) {
                                latestUiState.selectedSlotStarts - event.slotStart
                            } else {
                                latestUiState.selectedSlotStarts + event.slotStart
                            },
                        )
                    }
                }

                EventAvailabilityScreenEvent.SaveClicked -> {
                    if (eventId != null && eventDate != null) {
                        calendarViewModel.updateMyEventAvailability(
                            eventId = eventId,
                            date = eventDate.toString(),
                            timeSlots = mainUiState.eventAvailabilityUiState.selectedSlotStarts
                                .toAvailabilityTimeSlots(),
                        )
                    }
                }

                EventAvailabilityScreenEvent.ClearClicked -> {
                    navigator.push(MoilMainDestination.EventAvailabilityClearConfirmation)
                }

                EventAvailabilityScreenEvent.RetryClicked -> {
                    if (eventId != null && eventDate != null) {
                        calendarViewModel.loadEventAvailability(eventId, eventDate.toString())
                    }
                }
            }
        },
    )
}

/** 내 가능 시간 입력을 지우기 전 확인 다이얼로그 목적지다. */
@Composable
internal fun EventAvailabilityClearConfirmationRoute(
    mainUiState: MoilMainUiState,
    calendarViewModel: CalendarViewModel,
    navigator: MoilMainNavigator,
) {
    val availabilityUiState = mainUiState.eventAvailabilityUiState
    val eventId = availabilityUiState.eventId ?: return
    val eventDate = availabilityUiState.date ?: return

    MoilConfirmDialog(
        title = stringResource(
            R.string.availability_clear_title,
            eventDate.monthValue,
            eventDate.dayOfMonth,
        ),
        confirmLabel = stringResource(R.string.availability_clear_action),
        tone = MoilConfirmDialogTone.Destructive,
        onConfirm = {
            navigator.goBack()
            calendarViewModel.deleteMyEventAvailability(eventId, eventDate.toString())
        },
        onDismissRequest = navigator::goBack,
    )
}
