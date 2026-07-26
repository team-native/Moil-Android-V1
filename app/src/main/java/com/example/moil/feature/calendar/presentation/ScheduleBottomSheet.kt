package com.example.moil.feature.calendar.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.ui.component.CommonSwitch
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.LocalMoilExtraTypography
import com.example.moil.ui.theme.MoilRadius
import com.example.moil.ui.theme.MoilScheduleSheet
import com.example.moil.ui.theme.MoilTheme
import java.time.LocalTime

enum class CalendarMemberId { Mom, Me, Sibling, Dad }

private val calendarMembers = listOf(
    CalendarMember(CalendarMemberId.Dad, R.string.family_member_dad, R.drawable.family_avatar_dad),
    CalendarMember(CalendarMemberId.Mom, R.string.family_member_mom, R.drawable.family_avatar_mom),
    CalendarMember(CalendarMemberId.Me, R.string.family_member_me, R.drawable.family_avatar_mine),
    CalendarMember(CalendarMemberId.Sibling, R.string.family_member_sister, R.drawable.family_avatar_sibling),
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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
                value = uiState.scheduleTime.displayRange(),
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

            Text(
                text = stringResource(R.string.schedule_share_with),
                modifier = Modifier.padding(horizontal = MoilScheduleSheet.HorizontalPadding),
                color = MaterialTheme.colorScheme.outline,
                style = LocalMoilExtraTypography.current.scheduleShareLabel,
            )

            Spacer(modifier = Modifier.height(MoilScheduleSheet.SharedHeadingGap))

            SharedMemberSelector(
                sharedMemberIds = uiState.sharedMemberIds,
                onMemberClick = { onEvent(CalendarScreenEvent.SharedMemberClicked(it)) },
            )
        }
    }
}

@Composable
private fun ScheduleSheetDragHandle() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = MoilScheduleSheet.DragHandleWidth,
                    height = MoilScheduleSheet.DragHandleHeight,
                )
                .clip(RoundedCornerShape(percent = 50))
                .background(LocalMoilExtraColors.current.scheduleDivider),
        )
    }
}

@Composable
private fun ScheduleSheetHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(MoilScheduleSheet.HeaderHeight)
            .padding(horizontal = MoilScheduleSheet.HorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.schedule_cancel),
            modifier = Modifier.clickable(role = Role.Button, onClick = onDismiss),
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.schedule_new),
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = stringResource(R.string.schedule_save),
            modifier = Modifier.clickable(role = Role.Button, onClick = onDismiss),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ScheduleTitleField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val extraColors = LocalMoilExtraColors.current
    val extraTypography = LocalMoilExtraTypography.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(MoilScheduleSheet.TitleHeight)
            .padding(horizontal = MoilScheduleSheet.HorizontalPadding),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = extraTypography.scheduleTitle.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(R.string.schedule_title_placeholder),
                            color = extraColors.scheduleMutedText,
                            style = extraTypography.scheduleTitle,
                        )
                    }

                    innerTextField()
                },
            )
        }

        HorizontalDivider(color = extraColors.scheduleDivider)
    }
}

@Composable
private fun ScheduleInfoRow(
    @StringRes labelRes: Int,
    value: String,
    onClick: (() -> Unit)? = null,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.outline,
    showDivider: Boolean = true,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    if (showDivider) {
                        MoilScheduleSheet.FormRowHeight - 1.dp
                    } else {
                        MoilScheduleSheet.FormRowHeight
                    },
                )
                .then(
                    if (onClick != null) {
                        Modifier.clickable(role = Role.Button, onClick = onClick)
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = MoilScheduleSheet.HorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(labelRes),
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = value,
                color = valueColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if (showDivider) {
            HorizontalDivider(color = LocalMoilExtraColors.current.scheduleDivider)
        }
    }
}

@Composable
private fun ScheduleAllDayRow(
    isAllDay: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilScheduleSheet.AllDayRowHeight - 1.dp)
                .padding(horizontal = MoilScheduleSheet.HorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.schedule_all_day),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
            )

            CommonSwitch(
                checked = isAllDay,
                onCheckedChange = onCheckedChange,
            )
        }

        HorizontalDivider(color = LocalMoilExtraColors.current.scheduleDivider)
    }
}

@Composable
private fun SharedMemberSelector(
    sharedMemberIds: Set<CalendarMemberId>,
    onMemberClick: (CalendarMemberId) -> Unit,
) {
    val selectedMemberRingColor = MaterialTheme.colorScheme.primary
    val selectedMemberRingGapColor = MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MoilScheduleSheet.HorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(MoilScheduleSheet.SharedMemberSpacing),
    ) {
        calendarMembers.forEach { calendarMember ->
            val isSelected = calendarMember.id in sharedMemberIds
            val memberName = stringResource(calendarMember.nameRes)
            Column(
                modifier = Modifier
                    .width(MoilScheduleSheet.SharedMemberAvatarSize)
                    .clickable(role = Role.Checkbox) { onMemberClick(calendarMember.id) }
                    .semantics {
                        contentDescription = memberName
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(MoilScheduleSheet.SharedMemberAvatarSize)
                        .drawBehind {
                            if (isSelected) {
                                val avatarRadius = size.minDimension / 2
                                val primaryRingRadius = avatarRadius +
                                    MoilScheduleSheet.SelectedMemberWhiteRing.toPx() +
                                    MoilScheduleSheet.SelectedMemberPrimaryRing.toPx()
                                val whiteRingRadius = avatarRadius +
                                    MoilScheduleSheet.SelectedMemberWhiteRing.toPx()

                                drawCircle(
                                    color = selectedMemberRingColor,
                                    radius = primaryRingRadius,
                                )
                                drawCircle(
                                    color = selectedMemberRingGapColor,
                                    radius = whiteRingRadius,
                                )
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(calendarMember.avatarRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(MoilScheduleSheet.SharedMemberAvatarSize)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(modifier = Modifier.height(MoilScheduleSheet.SharedMemberLabelGap))

                Text(
                    text = memberName,
                    style = LocalMoilExtraTypography.current.scheduleMemberName.copy(
                        fontWeight = if (isSelected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                    ),
                )
            }
        }
    }
}

@Composable
private fun LocalTime.displayRange(): String {
    val endTime = plusHours(1)

    return stringResource(
        R.string.schedule_time_range,
        displayKoreanTime(),
        endTime.displayKoreanTime(),
    )
}

@Composable
private fun LocalTime.displayKoreanTime(): String {
    val periodRes = if (hour < 12) {
        R.string.schedule_time_am
    } else {
        R.string.schedule_time_pm
    }
    val displayHour = when (val hourInTwelveHourFormat = hour % 12) {
        0 -> 12
        else -> hourInTwelveHourFormat
    }

    return stringResource(periodRes, displayHour, minute)
}

@StringRes
private fun weekdayNameRes(date: java.time.LocalDate): Int = when (date.dayOfWeek) {
    java.time.DayOfWeek.SUNDAY -> R.string.calendar_weekday_sunday
    java.time.DayOfWeek.MONDAY -> R.string.calendar_weekday_monday
    java.time.DayOfWeek.TUESDAY -> R.string.calendar_weekday_tuesday
    java.time.DayOfWeek.WEDNESDAY -> R.string.calendar_weekday_wednesday
    java.time.DayOfWeek.THURSDAY -> R.string.calendar_weekday_thursday
    java.time.DayOfWeek.FRIDAY -> R.string.calendar_weekday_friday
    java.time.DayOfWeek.SATURDAY -> R.string.calendar_weekday_saturday
}

private data class CalendarMember(
    val id: CalendarMemberId,
    @param:StringRes val nameRes: Int,
    @param:DrawableRes val avatarRes: Int,
)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleBottomSheetPreview() {
    MoilTheme(darkTheme = false) {
        ScheduleBottomSheet(
            uiState = CalendarUiState(
                displayedMonth = java.time.YearMonth.of(2026, 7),
                selectedDate = java.time.LocalDate.of(2026, 7, 22),
                isScheduleSheetVisible = true,
                sharedMemberIds = setOf(CalendarMemberId.Mom),
            ),
            sheetState = androidx.compose.material3.rememberModalBottomSheetState(),
            onEvent = {},
        )
    }
}
