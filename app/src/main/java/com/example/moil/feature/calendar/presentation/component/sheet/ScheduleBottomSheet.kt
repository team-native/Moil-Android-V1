package com.example.moil.feature.calendar.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.feature.calendar.presentation.component.schedule.ScheduleAllDayRow
import com.example.moil.feature.calendar.presentation.component.schedule.ScheduleInfoRow
import com.example.moil.feature.calendar.presentation.component.schedule.ScheduleSheetDragHandle
import com.example.moil.feature.calendar.presentation.component.schedule.ScheduleSheetHeader
import com.example.moil.feature.calendar.presentation.component.schedule.ScheduleTitleField
import com.example.moil.feature.calendar.presentation.component.schedule.SharedMemberSelector
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.LocalMoilExtraTypography
import com.example.moil.ui.theme.MoilRadius
import com.example.moil.ui.theme.MoilScheduleSheet
import com.example.moil.ui.theme.MoilTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ScheduleBottomSheet(
    uiState: CalendarUiState,
    sheetState: SheetState,
    onEvent: (CalendarScreenEvent) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onEvent(CalendarScreenEvent.ScheduleSheetDismissed) },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(
            topStart = MoilRadius.ScheduleSheet,
            topEnd = MoilRadius.ScheduleSheet,
        ),
        dragHandle = null,
        contentWindowInsets = { WindowInsets.statusBars },
    ) {
        Column(modifier = Modifier.padding(bottom = MoilScheduleSheet.BottomPadding)) {
            Spacer(modifier = Modifier.height(MoilScheduleSheet.DragHandleTopPadding))

            ScheduleSheetDragHandle()

            Spacer(modifier = Modifier.height(MoilScheduleSheet.HeaderTopGap))

            ScheduleSheetHeader(
                onDismiss = { onEvent(CalendarScreenEvent.ScheduleSheetDismissed) },
                onSave = { onEvent(CalendarScreenEvent.ScheduleSaveClicked) },
            )

            ScheduleTitleField(
                value = uiState.scheduleTitle,
                onValueChange = { onEvent(CalendarScreenEvent.ScheduleTitleChanged(it)) },
            )

            ScheduleInfoRow(
                labelRes = R.string.schedule_date,
                value = stringResource(
                    R.string.schedule_date_value,
                    uiState.selectedDate.monthValue,
                    uiState.selectedDate.dayOfMonth,
                    stringResource(weekdayNameRes(uiState.selectedDate)),
                ),
                onClick = { onEvent(CalendarScreenEvent.ScheduleDateClicked) },
            )

            ScheduleAllDayRow(
                isAllDay = uiState.isAllDay,
                onCheckedChange = { onEvent(CalendarScreenEvent.AllDayChanged(it)) },
            )

            ScheduleInfoRow(
                labelRes = R.string.schedule_time,
                value = uiState.scheduleTime.toScheduleDisplayTime(),
                onClick = { onEvent(CalendarScreenEvent.ScheduleTimeClicked) },
            )
            ScheduleInfoRow(
                labelRes = R.string.schedule_location,
                value = uiState.scheduleLocation.ifEmpty {
                    stringResource(R.string.schedule_add)
                },
                valueColor = if (uiState.scheduleLocation.isEmpty()) {
                    LocalMoilExtraColors.current.scheduleMutedText
                } else {
                    MaterialTheme.colorScheme.outline
                },
                onClick = { onEvent(CalendarScreenEvent.ScheduleLocationClicked) },
                showDivider = false,
            )

            Spacer(modifier = Modifier.height(MoilScheduleSheet.SharedSectionTopPadding))

            androidx.compose.material3.Text(
                text = stringResource(R.string.schedule_share_with),
                modifier = Modifier.padding(horizontal = MoilScheduleSheet.HorizontalPadding),
                color = MaterialTheme.colorScheme.outline,
                style = LocalMoilExtraTypography.current.scheduleShareLabel,
            )

            Spacer(modifier = Modifier.height(MoilScheduleSheet.SharedHeadingGap))

            SharedMemberSelector(
                members = uiState.members,
                sharedMemberIds = uiState.sharedMemberIds,
                onMemberClick = { onEvent(CalendarScreenEvent.SharedMemberClicked(it)) },
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleBottomSheetPreview() {
    MoilTheme(darkTheme = false) {
        ScheduleBottomSheet(
            uiState = CalendarUiState(
                displayedMonth = java.time.YearMonth.of(2026, 7),
                selectedDate = java.time.LocalDate.of(2026, 7, 22),
                isScheduleSheetVisible = true,
                sharedMemberIds = setOf(1L),
            ),
            sheetState = rememberModalBottomSheetState(),
            onEvent = {},
        )
    }
}
