package com.example.moil.feature.calendar.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.moil.R
import com.example.moil.core.component.display.MoilRemoteAvatar
import com.example.moil.feature.calendar.viewmodel.CalendarScheduleSheetMode
import com.example.moil.feature.calendar.viewmodel.CalendarScheduleMemberUiModel
import com.example.moil.feature.calendar.viewmodel.CalendarScheduleUiModel
import com.example.moil.feature.calendar.viewmodel.CalendarScreenEvent
import com.example.moil.feature.calendar.viewmodel.CalendarUiState
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.LocalMoilExtraTypography
import com.example.moil.ui.theme.MoilRadius
import com.example.moil.ui.theme.MoilScheduleSheet
import com.example.moil.ui.theme.MoilTheme
import java.time.LocalDate
import java.time.LocalTime

/** 일정 목록·생성·수정·상세를 하나의 ModalBottomSheet 안에서 전환합니다. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ScheduleBottomSheet(
    uiState: CalendarUiState,
    sheetState: SheetState,
    selectedSchedule: CalendarScheduleUiModel?,
    isSelectedEventLoading: Boolean,
    isMutationLoading: Boolean,
    hasSelectedEventError: Boolean,
    hasMutationError: Boolean,
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
        when (uiState.scheduleSheetMode) {
            CalendarScheduleSheetMode.List -> ScheduleListContent(
                selectedDate = uiState.selectedDate,
                schedules = uiState.schedules,
                onCreateClick = {
                    onEvent(CalendarScreenEvent.ScheduleCreateClicked)
                },
                onScheduleClick = { eventId ->
                    onEvent(CalendarScreenEvent.ScheduleItemClicked(eventId))
                },
            )

            CalendarScheduleSheetMode.Create,
            CalendarScheduleSheetMode.Edit -> ScheduleFormContent(
                uiState = uiState,
                isEditMode = uiState.scheduleSheetMode == CalendarScheduleSheetMode.Edit,
                isMutationLoading = isMutationLoading,
                hasMutationError = hasMutationError,
                onEvent = onEvent,
            )

            CalendarScheduleSheetMode.Detail -> ScheduleDetailContent(
                schedule = selectedSchedule,
                isLoading = isSelectedEventLoading,
                hasError = hasSelectedEventError,
                hasMutationError = hasMutationError,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun ScheduleListContent(
    selectedDate: LocalDate,
    schedules: List<CalendarScheduleUiModel>,
    onCreateClick: () -> Unit,
    onScheduleClick: (Long) -> Unit,
) {
    val selectedDateSchedules = schedules
        .filter { schedule -> schedule.date == selectedDate }
        .sortedWith(compareBy<CalendarScheduleUiModel> { it.startTime ?: LocalTime.MAX }.thenBy { it.id })

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        item {
            ScheduleListHeader(
                selectedDate = selectedDate,
                onCreateClick = onCreateClick,
            )
        }

        if (selectedDateSchedules.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MoilScheduleSheet.ListItemHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.schedule_empty_date),
                        color = LocalMoilExtraColors.current.scheduleMutedText,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        } else {
            items(
                items = selectedDateSchedules,
                key = { schedule -> schedule.id },
            ) { schedule ->
                ScheduleListRow(
                    schedule = schedule,
                    onClick = { onScheduleClick(schedule.id) },
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(MoilScheduleSheet.BottomPadding))
        }
    }
}

@Composable
private fun ScheduleListHeader(
    selectedDate: LocalDate,
    onCreateClick: () -> Unit,
) {
    val createContentDescription = stringResource(R.string.schedule_create_content_description)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(MoilScheduleSheet.ListHeaderHeight)
            .padding(horizontal = MoilScheduleSheet.HorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(
                        R.string.schedule_date_header,
                        selectedDate.monthValue,
                        selectedDate.dayOfMonth,
                        stringResource(weekdayNameRes(selectedDate)),
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                if (selectedDate == LocalDate.now()) {
                    Spacer(modifier = Modifier.width(MoilScheduleSheet.ListContentSpacing))

                    Text(
                        text = stringResource(R.string.schedule_today),
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(MoilScheduleSheet.ListCreateButtonSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(
                    role = Role.Button,
                    onClick = onCreateClick,
                )
                .semantics {
                    contentDescription = createContentDescription
                },
            contentAlignment = Alignment.Center,
        ) {
            // 버튼(60dp)만 커진 만큼 "+" 심볼도 같은 비율(25%)로 이 버튼 안에서만 확대한다.
            // headlineMedium 자체는 다른 화면에서도 공용으로 쓰이므로 건드리지 않는다.
            Text(
                text = stringResource(R.string.schedule_add_symbol),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 25.sp,
                    lineHeight = 32.5.sp,
                ),
                fontWeight = FontWeight.Light,
            )
        }
    }
}

@Composable
private fun ScheduleListRow(
    schedule: CalendarScheduleUiModel,
    onClick: () -> Unit,
) {
    val extraColors = LocalMoilExtraColors.current

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilScheduleSheet.ListItemHeight)
                .clickable(
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(horizontal = MoilScheduleSheet.HorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(MoilScheduleSheet.ListEventDotSize)
                    .clip(CircleShape)
                    .background(extraColors.colorForProfile(schedule.displayColor)),
            )

            Spacer(modifier = Modifier.width(MoilScheduleSheet.ListContentSpacing))

            ScheduleListTime(
                startTime = schedule.startTime,
                endTime = schedule.endTime,
                modifier = Modifier.width(MoilScheduleSheet.ListTimeWidth),
            )

            Spacer(modifier = Modifier.width(MoilScheduleSheet.ListContentSpacing))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                schedule.location?.takeIf(String::isNotBlank)?.let { location ->
                    Text(
                        text = location,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Spacer(modifier = Modifier.height(MoilScheduleSheet.ListContentSpacing))

                ScheduleParticipantAvatars(
                    members = schedule.members,
                    contentDescription = stringResource(
                        R.string.schedule_participants_content_description,
                        schedule.members.size,
                    ),
                )
            }

            Text(
                text = stringResource(R.string.schedule_next_symbol),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineLarge,
            )
        }

        HorizontalDivider(color = extraColors.scheduleDivider)
    }
}

@Composable
private fun ScheduleListTime(
    startTime: LocalTime?,
    endTime: LocalTime?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (startTime == null && endTime == null) {
            Text(
                text = stringResource(R.string.schedule_all_day),
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodySmall,
            )
        } else {
            Text(
                text = startTime?.toShortScheduleTime() ?: stringResource(R.string.schedule_time_unknown_short),
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = endTime?.toShortScheduleTime() ?: stringResource(R.string.schedule_time_unknown_short),
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun ScheduleFormContent(
    uiState: CalendarUiState,
    isEditMode: Boolean,
    isMutationLoading: Boolean,
    hasMutationError: Boolean,
    onEvent: (CalendarScreenEvent) -> Unit,
) {
    Column(
        modifier = Modifier.padding(bottom = MoilScheduleSheet.BottomPadding),
    ) {
        Spacer(modifier = Modifier.height(MoilScheduleSheet.DragHandleTopPadding))
        ScheduleSheetDragHandle()
        Spacer(modifier = Modifier.height(MoilScheduleSheet.HeaderTopGap))

        ScheduleSheetHeader(
            titleRes = if (isEditMode) R.string.schedule_edit else R.string.schedule_new,
            onDismiss = {
                onEvent(
                    if (isEditMode) {
                        CalendarScreenEvent.ScheduleEditCanceled
                    } else {
                        CalendarScreenEvent.ScheduleSheetDismissed
                    },
                )
            },
            onSave = {
                if (!isMutationLoading) {
                    onEvent(CalendarScreenEvent.ScheduleSaveClicked)
                }
            },
        )

        ScheduleTitleField(
            value = uiState.scheduleTitle,
            onValueChange = { title -> onEvent(CalendarScreenEvent.ScheduleTitleChanged(title)) },
        )

        ScheduleInfoRow(
            labelRes = R.string.schedule_date,
            value = stringResource(
                R.string.schedule_date_value,
                uiState.scheduleDate.monthValue,
                uiState.scheduleDate.dayOfMonth,
                stringResource(weekdayNameRes(uiState.scheduleDate)),
            ),
            onClick = { onEvent(CalendarScreenEvent.ScheduleDateClicked) },
        )

        ScheduleInfoRow(
            labelRes = R.string.schedule_time,
            value = formatScheduleTimeRange(
                startTime = uiState.scheduleStartTime,
                endTime = uiState.scheduleEndTime,
            ),
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
        )

        ScheduleInfoRow(
            labelRes = R.string.schedule_memo,
            value = uiState.scheduleMemo.ifEmpty {
                stringResource(R.string.schedule_add)
            },
            valueColor = if (uiState.scheduleMemo.isEmpty()) {
                LocalMoilExtraColors.current.scheduleMutedText
            } else {
                MaterialTheme.colorScheme.outline
            },
            onClick = { onEvent(CalendarScreenEvent.ScheduleMemoClicked) },
        )

        if (hasMutationError) {
            Text(
                text = stringResource(R.string.schedule_action_error),
                modifier = Modifier.padding(horizontal = MoilScheduleSheet.HorizontalPadding),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(MoilScheduleSheet.SharedSectionTopPadding))

        Text(
            text = stringResource(R.string.schedule_share_with),
            modifier = Modifier.padding(horizontal = MoilScheduleSheet.HorizontalPadding),
            color = MaterialTheme.colorScheme.outline,
            style = LocalMoilExtraTypography.current.scheduleShareLabel,
        )

        Spacer(modifier = Modifier.height(MoilScheduleSheet.SharedHeadingGap))

        SharedMemberSelector(
            members = uiState.members,
            sharedMemberIds = uiState.sharedMemberIds,
            onMemberClick = { memberId ->
                onEvent(CalendarScreenEvent.SharedMemberClicked(memberId))
            },
        )

        if (isMutationLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = MoilScheduleSheet.ListContentSpacing)
                    .align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
private fun ScheduleDetailContent(
    schedule: CalendarScheduleUiModel?,
    isLoading: Boolean,
    hasError: Boolean,
    hasMutationError: Boolean,
    onEvent: (CalendarScreenEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = MoilScheduleSheet.HorizontalPadding,
                end = MoilScheduleSheet.HorizontalPadding,
                bottom = MoilScheduleSheet.BottomPadding,
            ),
    ) {
        Spacer(modifier = Modifier.height(MoilScheduleSheet.DragHandleTopPadding))
        ScheduleSheetDragHandle()
        Spacer(modifier = Modifier.height(MoilScheduleSheet.HeaderTopGap))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (schedule == null || hasError) {
            Text(
                text = stringResource(R.string.schedule_detail_load_error),
                modifier = Modifier.padding(vertical = MoilScheduleSheet.DetailSectionTopPadding),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
            ScheduleDetailCloseButton(onClick = { onEvent(CalendarScreenEvent.ScheduleSheetDismissed) })
        } else {
            ScheduleDetailHeader(
                title = schedule.title,
                onClose = { onEvent(CalendarScreenEvent.ScheduleSheetDismissed) },
            )

            ScheduleDetailInfoRow(
                label = stringResource(R.string.schedule_type),
                value = stringResource(R.string.schedule_type_value),
            )
            ScheduleDetailInfoRow(
                label = stringResource(R.string.schedule_date),
                value = stringResource(
                    R.string.schedule_detail_date_value,
                    schedule.date.year,
                    schedule.date.monthValue,
                    schedule.date.dayOfMonth,
                    stringResource(weekdayNameRes(schedule.date)),
                ),
            )
            ScheduleDetailInfoRow(
                label = stringResource(R.string.schedule_time),
                value = formatScheduleTimeRange(schedule.startTime, schedule.endTime),
            )
            ScheduleDetailInfoRow(
                label = stringResource(R.string.schedule_location),
                value = schedule.location?.takeIf(String::isNotBlank)
                    ?: stringResource(R.string.schedule_no_location),
                showMapAction = !schedule.location.isNullOrBlank(),
            )

            HorizontalDivider(
                modifier = Modifier.padding(top = MoilScheduleSheet.ListContentSpacing),
                color = LocalMoilExtraColors.current.scheduleDivider,
            )

            Text(
                text = stringResource(R.string.schedule_participants_count, schedule.members.size),
                modifier = Modifier.padding(top = MoilScheduleSheet.DetailSectionTopPadding),
                style = MaterialTheme.typography.bodyMedium,
            )
            ScheduleParticipantAvatars(
                members = schedule.members,
                modifier = Modifier.padding(top = MoilScheduleSheet.ListContentSpacing),
                contentDescription = stringResource(
                    R.string.schedule_participants_content_description,
                    schedule.members.size,
                ),
            )

            HorizontalDivider(
                modifier = Modifier.padding(top = MoilScheduleSheet.DetailSectionTopPadding),
                color = LocalMoilExtraColors.current.scheduleDivider,
            )

            ScheduleDetailMemo(
                memo = schedule.memo,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MoilScheduleSheet.DetailSectionTopPadding),
                horizontalArrangement = Arrangement.spacedBy(MoilScheduleSheet.DetailButtonSpacing),
            ) {
                Button(
                    onClick = { onEvent(CalendarScreenEvent.ScheduleDetailEditClicked) },
                    modifier = Modifier
                        .weight(1f)
                        .height(MoilScheduleSheet.DetailButtonHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                ) {
                    Text(text = stringResource(R.string.schedule_edit))
                }
                Button(
                    onClick = { onEvent(CalendarScreenEvent.ScheduleDetailDeleteClicked) },
                    modifier = Modifier
                        .weight(1f)
                        .height(MoilScheduleSheet.DetailButtonHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text(text = stringResource(R.string.schedule_delete))
                }
            }

            if (hasMutationError) {
                Text(
                    text = stringResource(R.string.schedule_action_error),
                    modifier = Modifier.padding(top = MoilScheduleSheet.ListContentSpacing),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun ScheduleDetailHeader(
    title: String,
    onClose: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.schedule_type_value),
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            ScheduleDetailCloseButton(onClick = onClose)
        }

        Text(
            text = title,
            modifier = Modifier.padding(top = MoilScheduleSheet.DetailTitleTopGap),
            style = LocalMoilExtraTypography.current.scheduleTitle,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ScheduleDetailCloseButton(onClick: () -> Unit) {
    val closeContentDescription = stringResource(R.string.schedule_close_content_description)

    Text(
        text = stringResource(R.string.schedule_close_symbol),
        modifier = Modifier
            .size(MoilScheduleSheet.ListAddButtonSize)
            .wrapContentSize(Alignment.Center)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                contentDescription = closeContentDescription
            },
        color = MaterialTheme.colorScheme.outline,
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Composable
private fun ScheduleDetailInfoRow(
    label: String,
    value: String,
    showMapAction: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MoilScheduleSheet.ListContentSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
        )
        if (showMapAction) {
            Text(
                text = stringResource(R.string.schedule_map),
                modifier = Modifier.padding(start = MoilScheduleSheet.ListContentSpacing),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun ScheduleDetailMemo(memo: String?) {
    Column(modifier = Modifier.padding(top = MoilScheduleSheet.DetailSectionTopPadding)) {
        Text(
            text = stringResource(R.string.schedule_memo),
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = memo?.takeIf(String::isNotBlank)
                ?: stringResource(R.string.schedule_no_memo),
            modifier = Modifier.padding(top = MoilScheduleSheet.ListContentSpacing),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun ScheduleParticipantAvatars(
    members: List<CalendarScheduleMemberUiModel>,
    modifier: Modifier = Modifier,
    contentDescription: String,
) {
    Row(
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
        },
        horizontalArrangement = Arrangement.spacedBy(MoilScheduleSheet.ListAvatarSpacing),
    ) {
        members.take(MoilScheduleSheet.ListVisibleAvatarCount).forEach { member ->
            MoilRemoteAvatar(
                imagePath = member.imagePath,
                fallbackAvatarRes = member.avatarRes,
                contentDescription = null,
                size = MoilScheduleSheet.ListAvatarSize,
            )
        }

        val remainingMemberCount = members.size - MoilScheduleSheet.ListVisibleAvatarCount
        if (remainingMemberCount > 0) {
            Box(
                modifier = Modifier
                    .size(MoilScheduleSheet.ListAvatarSize)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.schedule_more_members, remainingMemberCount),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ScheduleBottomSheetPreview() {
    MoilTheme(darkTheme = false) {
        ScheduleListContent(
            selectedDate = LocalDate.of(2026, 7, 15),
            schedules = listOf(
                CalendarScheduleUiModel(
                    id = 1L,
                    title = "디자인 회의",
                    date = LocalDate.of(2026, 7, 15),
                    startTime = LocalTime.of(10, 0),
                    endTime = LocalTime.of(11, 30),
                    location = "회의실 A",
                    memo = "신규 서비스 리뷰",
                    members = emptyList(),
                    displayColor = com.example.moil.feature.group.module.domain.model.GroupColor.Sky,
                ),
            ),
            onCreateClick = {},
            onScheduleClick = {},
        )
    }
}
