package com.example.moil.feature.calendar.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.core.component.toUserMessage
import com.example.moil.feature.calendar.viewmodel.EventAttendanceUiModel
import com.example.moil.feature.calendar.viewmodel.EventSectionLoadState
import com.example.moil.feature.event.module.domain.model.EventAttendanceChoice
import com.example.moil.feature.event.module.domain.model.EventAttendanceStatus
import com.example.moil.ui.theme.MoilAuthDimension
import com.example.moil.ui.theme.MoilOverlayDimension
import com.example.moil.ui.theme.MoilScheduleSheet
import com.example.moil.ui.theme.MoilTheme

/**
 * 일정 상세 시트의 참석 여부 섹션이다.
 *
 * 서버 계약상 응답은 참석·불참 두 가지이며, 응답하지 않은 상태가 미응답이다.
 * 이미 고른 버튼을 다시 눌러도 취소되지 않고, 취소는 별도 "응답 취소"로만 할 수 있게 해 실수를 줄인다.
 */
@Composable
internal fun EventAttendanceSection(
    attendance: EventAttendanceUiModel,
    onChoiceClick: (EventAttendanceChoice) -> Unit,
    onCancelClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resources = LocalResources.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.schedule_attendance_title),
            style = MaterialTheme.typography.bodyMedium,
        )

        when (attendance.loadState) {
            EventSectionLoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = MoilScheduleSheet.ListContentSpacing)
                        .size(MoilOverlayDimension.DialogProgressSize),
                    strokeWidth = MoilOverlayDimension.DialogProgressStrokeWidth,
                )
            }

            EventSectionLoadState.Failed -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.schedule_attendance_load_error),
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )

                    EventAttendanceTextAction(
                        text = stringResource(R.string.calendar_retry),
                        isEnabled = true,
                        onClick = onRetryClick,
                    )
                }
            }

            EventSectionLoadState.Loaded -> {
                Text(
                    text = stringResource(
                        R.string.schedule_attendance_summary,
                        attendance.attendingCount,
                        attendance.declinedCount,
                        attendance.pendingCount,
                    ),
                    modifier = Modifier.padding(top = MoilScheduleSheet.ListContentSpacing),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MoilScheduleSheet.ListContentSpacing),
                    horizontalArrangement = Arrangement.spacedBy(MoilScheduleSheet.DetailButtonSpacing),
                ) {
                    EventAttendanceChoiceButton(
                        text = stringResource(R.string.schedule_attendance_attend),
                        isSelected = attendance.myStatus == EventAttendanceStatus.Attending,
                        isEnabled = !attendance.isMutating,
                        onClick = { onChoiceClick(EventAttendanceChoice.Attending) },
                        modifier = Modifier.weight(1f),
                    )

                    EventAttendanceChoiceButton(
                        text = stringResource(R.string.schedule_attendance_decline),
                        isSelected = attendance.myStatus == EventAttendanceStatus.Declined,
                        isEnabled = !attendance.isMutating,
                        onClick = { onChoiceClick(EventAttendanceChoice.Declined) },
                        modifier = Modifier.weight(1f),
                    )
                }

                if (attendance.myStatus != null && attendance.myStatus != EventAttendanceStatus.Unknown) {
                    EventAttendanceTextAction(
                        text = stringResource(R.string.schedule_attendance_cancel),
                        isEnabled = !attendance.isMutating,
                        onClick = onCancelClick,
                    )
                }
            }
        }

        attendance.mutationError?.let { error ->
            Text(
                text = error.toUserMessage(resources),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun EventAttendanceChoiceButton(
    text: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(MoilScheduleSheet.DetailButtonHeight)
            .selectable(
                selected = isSelected,
                enabled = isEnabled,
                role = Role.RadioButton,
                onClick = {
                    if (!isSelected) {
                        onClick()
                    }
                },
            ),
        shape = RoundedCornerShape(MoilScheduleSheet.DetailButtonSpacing),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun EventAttendanceTextAction(
    text: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .defaultMinSize(minHeight = MoilAuthDimension.ActionMinTouchTarget)
            .clickable(
                enabled = isEnabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EventAttendanceSectionPreview() {
    MoilTheme(darkTheme = true) {
        EventAttendanceSection(
            attendance = EventAttendanceUiModel(
                loadState = EventSectionLoadState.Loaded,
                myStatus = EventAttendanceStatus.Attending,
                attendingCount = 2,
                declinedCount = 1,
                pendingCount = 1,
            ),
            onChoiceClick = {},
            onCancelClick = {},
            onRetryClick = {},
        )
    }
}
