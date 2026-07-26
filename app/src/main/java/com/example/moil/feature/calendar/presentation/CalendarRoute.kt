package com.example.moil.feature.calendar.presentation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.MoilNavigationDestination
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarRoute() {
    var selectedDestination by remember { mutableStateOf(MoilNavigationDestination.Calendar) }
    var calendarUiState by remember {
        mutableStateOf(
            CalendarUiState(
                displayedMonth = YearMonth.of(2026, 7),
                selectedDate = LocalDate.of(2026, 7, 22),
            ),
        )
    }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var isScheduleDatePickerVisible by remember { mutableStateOf(false) }
    var isScheduleTimePickerVisible by remember { mutableStateOf(false) }
    var isScheduleLocationDialogVisible by remember { mutableStateOf(false) }

    when (selectedDestination) {
        MoilNavigationDestination.Calendar -> {
            val scheduleSheetState = rememberModalBottomSheetState()
            var isScheduleSheetRendered by remember {
                mutableStateOf(calendarUiState.isScheduleSheetVisible)
            }

            LaunchedEffect(calendarUiState.isScheduleSheetVisible) {
                if (calendarUiState.isScheduleSheetVisible) {
                    isScheduleSheetRendered = true
                    scheduleSheetState.show()
                } else if (isScheduleSheetRendered) {
                    scheduleSheetState.hide()
                    isScheduleSheetRendered = false
                }
            }

            CalendarScreen(
                uiState = calendarUiState,
                onEvent = { calendarScreenEvent ->
                    calendarUiState = calendarUiState.reduce(calendarScreenEvent)
                },
                onDestinationClick = { destination ->
                    selectedDestination = destination
                },
            )

            if (isScheduleSheetRendered) {
                ScheduleBottomSheet(
                    uiState = calendarUiState,
                    sheetState = scheduleSheetState,
                    onEvent = { calendarScreenEvent ->
                        when (calendarScreenEvent) {
                            CalendarScreenEvent.ScheduleDateClicked -> {
                                isScheduleDatePickerVisible = true
                            }

                            CalendarScreenEvent.ScheduleTimeClicked -> {
                                isScheduleTimePickerVisible = true
                            }

                            CalendarScreenEvent.ScheduleLocationClicked -> {
                                isScheduleLocationDialogVisible = true
                            }

                            else -> {
                                calendarUiState = calendarUiState.reduce(calendarScreenEvent)
                            }
                        }
                    },
                )
            }

            if (calendarUiState.isScheduleSheetVisible) {
                if (isScheduleDatePickerVisible) {
                    ScheduleDatePickerDialog(
                        selectedDate = calendarUiState.selectedDate,
                        onDateConfirmed = { selectedDate ->
                            calendarUiState = calendarUiState.reduce(
                                CalendarScreenEvent.ScheduleDateChanged(selectedDate),
                            )
                            isScheduleDatePickerVisible = false
                        },
                        onDismiss = {
                            isScheduleDatePickerVisible = false
                        },
                    )
                }

                if (isScheduleTimePickerVisible) {
                    ScheduleTimePickerDialog(
                        selectedTime = calendarUiState.scheduleTime,
                        onTimeConfirmed = { selectedTime ->
                            calendarUiState = calendarUiState.reduce(
                                CalendarScreenEvent.ScheduleTimeChanged(selectedTime),
                            )
                            isScheduleTimePickerVisible = false
                        },
                        onDismiss = {
                            isScheduleTimePickerVisible = false
                        },
                    )
                }

                if (isScheduleLocationDialogVisible) {
                    ScheduleLocationDialog(
                        initialLocation = calendarUiState.scheduleLocation,
                        onLocationConfirmed = { location ->
                            calendarUiState = calendarUiState.reduce(
                                CalendarScreenEvent.ScheduleLocationChanged(location),
                            )
                            isScheduleLocationDialogVisible = false
                        },
                        onDismiss = {
                            isScheduleLocationDialogVisible = false
                        },
                    )
                }
            }
        }

        MoilNavigationDestination.Family -> FamilyScreen(
            onDestinationClick = { selectedDestination = it },
        )

        MoilNavigationDestination.AddSchedule -> AddScheduleScreen(
            onDestinationClick = { selectedDestination = it },
        )

        MoilNavigationDestination.Profile -> ProfileScreen(
            notificationsEnabled = notificationsEnabled,
            onNotificationChange = { notificationsEnabled = it },
            onDestinationClick = { selectedDestination = it },
        )
    }
}

data class CalendarUiState(
    val displayedMonth: YearMonth,
    val selectedDate: LocalDate,
    val isGroupMenuVisible: Boolean = false,
    val isScheduleSheetVisible: Boolean = false,
    val scheduleTitle: String = "",
    val isAllDay: Boolean = false,
    val scheduleTime: LocalTime = LocalTime.of(9, 0),
    val scheduleLocation: String = "",
    val sharedMemberIds: Set<CalendarMemberId> = emptySet(),
)

sealed interface CalendarScreenEvent {
    data object PreviousMonthClicked : CalendarScreenEvent
    data object NextMonthClicked : CalendarScreenEvent
    data object GroupMenuClicked : CalendarScreenEvent
    data class DateClicked(val date: LocalDate) : CalendarScreenEvent
    data object ScheduleSheetDismissed : CalendarScreenEvent
    data class ScheduleTitleChanged(val title: String) : CalendarScreenEvent
    data object ScheduleDateClicked : CalendarScreenEvent
    data class ScheduleDateChanged(val date: LocalDate) : CalendarScreenEvent
    data class AllDayChanged(val isAllDay: Boolean) : CalendarScreenEvent
    data object ScheduleTimeClicked : CalendarScreenEvent
    data class ScheduleTimeChanged(val time: LocalTime) : CalendarScreenEvent
    data object ScheduleLocationClicked : CalendarScreenEvent
    data class ScheduleLocationChanged(val location: String) : CalendarScreenEvent
    data class SharedMemberClicked(val memberId: CalendarMemberId) : CalendarScreenEvent
}

private fun CalendarUiState.reduce(event: CalendarScreenEvent): CalendarUiState = when (event) {
    CalendarScreenEvent.PreviousMonthClicked -> copy(displayedMonth = displayedMonth.minusMonths(1))
    CalendarScreenEvent.NextMonthClicked -> copy(displayedMonth = displayedMonth.plusMonths(1))
    CalendarScreenEvent.GroupMenuClicked -> copy(isGroupMenuVisible = !isGroupMenuVisible)
    is CalendarScreenEvent.DateClicked -> copy(
        selectedDate = event.date,
        isGroupMenuVisible = false,
        isScheduleSheetVisible = true,
    )
    CalendarScreenEvent.ScheduleSheetDismissed -> copy(isScheduleSheetVisible = false)
    is CalendarScreenEvent.ScheduleTitleChanged -> copy(scheduleTitle = event.title)
    CalendarScreenEvent.ScheduleDateClicked,
    CalendarScreenEvent.ScheduleTimeClicked,
    CalendarScreenEvent.ScheduleLocationClicked -> this
    is CalendarScreenEvent.ScheduleDateChanged -> copy(selectedDate = event.date)
    is CalendarScreenEvent.AllDayChanged -> copy(isAllDay = event.isAllDay)
    is CalendarScreenEvent.ScheduleTimeChanged -> copy(scheduleTime = event.time)
    is CalendarScreenEvent.ScheduleLocationChanged -> copy(scheduleLocation = event.location)
    is CalendarScreenEvent.SharedMemberClicked -> copy(
        sharedMemberIds = sharedMemberIds.toggle(event.memberId),
    )
}

private fun Set<CalendarMemberId>.toggle(memberId: CalendarMemberId): Set<CalendarMemberId> =
    if (memberId in this) this - memberId else this + memberId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleDatePickerDialog(
    selectedDate: LocalDate,
    onDateConfirmed: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli(),
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                        onDateConfirmed(
                            Instant
                                .ofEpochMilli(selectedDateMillis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate(),
                        )
                    }
                },
            ) {
                Text(text = stringResource(R.string.schedule_save))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.schedule_cancel))
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleTimePickerDialog(
    selectedTime: LocalTime,
    onTimeConfirmed: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute,
        is24Hour = false,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onTimeConfirmed(
                        LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute,
                        ),
                    )
                },
            ) {
                Text(text = stringResource(R.string.schedule_save))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.schedule_cancel))
            }
        },
        text = {
            TimePicker(state = timePickerState)
        },
    )
}

@Composable
private fun ScheduleLocationDialog(
    initialLocation: String,
    onLocationConfirmed: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var locationInput by remember(initialLocation) {
        mutableStateOf(initialLocation)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onLocationConfirmed(locationInput)
                },
            ) {
                Text(text = stringResource(R.string.schedule_save))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.schedule_cancel))
            }
        },
        title = {
            Text(text = stringResource(R.string.schedule_location_dialog_title))
        },
        text = {
            OutlinedTextField(
                value = locationInput,
                onValueChange = { locationInput = it },
                placeholder = {
                    Text(text = stringResource(R.string.schedule_location_placeholder))
                },
                singleLine = true,
            )
        },
    )
}
