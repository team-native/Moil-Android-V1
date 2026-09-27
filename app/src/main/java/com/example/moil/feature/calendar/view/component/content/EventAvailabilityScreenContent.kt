package com.example.moil.feature.calendar.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.core.component.MoilPrimaryButton
import com.example.moil.core.component.toUserMessage
import com.example.moil.feature.calendar.viewmodel.EventAvailabilityDisplayModel
import com.example.moil.feature.calendar.viewmodel.EventAvailabilityScreenEvent
import com.example.moil.feature.calendar.viewmodel.EventAvailabilityTab
import com.example.moil.feature.calendar.viewmodel.EventAvailabilityUiState
import com.example.moil.feature.calendar.viewmodel.EventSectionLoadState
import com.example.moil.feature.calendar.viewmodel.toAvailabilityServerTime
import com.example.moil.feature.family.view.FamilyDetailHeader
import com.example.moil.ui.theme.MoilAuthDimension
import com.example.moil.ui.theme.MoilAvailabilityDimension
import com.example.moil.ui.theme.MoilProfileEditDimension
import com.example.moil.ui.theme.MoilTheme
import java.time.LocalDate

/**
 * 일정 날짜의 가능 시간 화면 본문이다.
 * "함께 보기"는 모두 되는 시간과 칸별 가능 인원을, "내 시간 입력"은 내 가능 시간 선택과 저장을 보여준다.
 */
@Composable
internal fun EventAvailabilityScreenContent(
    uiState: EventAvailabilityUiState,
    displayModel: EventAvailabilityDisplayModel,
    onEvent: (EventAvailabilityScreenEvent) -> Unit,
) {
    val resources = LocalResources.current
    val isMineTab = uiState.selectedTab == EventAvailabilityTab.Mine

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = MoilAvailabilityDimension.ScreenHorizontalPadding),
    ) {
        FamilyDetailHeader(
            groupName = stringResource(R.string.availability_title),
            onBackClick = { onEvent(EventAvailabilityScreenEvent.BackClicked) },
        )

        uiState.date?.let { eventDate ->
            Text(
                text = stringResource(
                    R.string.availability_subtitle,
                    uiState.eventTitle,
                    eventDate.monthValue,
                    eventDate.dayOfMonth,
                    stringResource(weekdayNameRes(eventDate)),
                ),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(MoilAvailabilityDimension.SectionSpacing))

        AvailabilityModeTabs(
            selectedTab = uiState.selectedTab,
            onTabSelected = { tab -> onEvent(EventAvailabilityScreenEvent.TabSelected(tab)) },
        )

        Spacer(modifier = Modifier.height(MoilAvailabilityDimension.SectionSpacing))

        when (displayModel.loadState) {
            EventSectionLoadState.Loading -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            EventSectionLoadState.Failed -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.availability_load_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    AvailabilityTextAction(
                        text = stringResource(R.string.calendar_retry),
                        onClick = { onEvent(EventAvailabilityScreenEvent.RetryClicked) },
                    )
                }
            }

            EventSectionLoadState.Loaded -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    if (isMineTab) {
                        Text(
                            text = stringResource(R.string.availability_mine_hint),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    } else if (displayModel.respondedCount == 0) {
                        Text(
                            text = stringResource(R.string.availability_empty),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        AvailabilityTextAction(
                            text = stringResource(R.string.availability_empty_action),
                            onClick = {
                                onEvent(EventAvailabilityScreenEvent.TabSelected(EventAvailabilityTab.Mine))
                            },
                        )
                    } else {
                        AvailabilitySummaryCard(displayModel = displayModel)

                        AvailabilityFocusedMembers(
                            displayModel = displayModel,
                            uiState = uiState,
                        )
                    }

                    Spacer(modifier = Modifier.height(MoilAvailabilityDimension.SectionSpacing))

                    AvailabilityGrid(
                        cells = displayModel.cells,
                        participantCount = displayModel.participantCount,
                        selectedSlotStarts = if (isMineTab) uiState.selectedSlotStarts else null,
                        focusedSlotStart = uiState.focusedSlotStart,
                        isEnabled = !displayModel.isMutating,
                        onSlotClick = { slotStart ->
                            onEvent(EventAvailabilityScreenEvent.SlotClicked(slotStart))
                        },
                    )

                    Spacer(modifier = Modifier.height(MoilAvailabilityDimension.SectionSpacing))
                }

                displayModel.mutationError?.let { error ->
                    Text(
                        text = error.toUserMessage(resources),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                if (isMineTab) {
                    if (displayModel.hasMyAvailability) {
                        AvailabilityTextAction(
                            text = stringResource(R.string.availability_clear),
                            onClick = { onEvent(EventAvailabilityScreenEvent.ClearClicked) },
                        )
                    }

                    MoilPrimaryButton(
                        text = stringResource(R.string.availability_save),
                        onClick = { onEvent(EventAvailabilityScreenEvent.SaveClicked) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(bottom = MoilProfileEditDimension.SaveButtonBottomPadding)
                            .height(MoilProfileEditDimension.SaveButtonHeight),
                        enabled = uiState.selectedSlotStarts.isNotEmpty() &&
                            uiState.selectedSlotStarts != displayModel.mySlotStarts &&
                            !displayModel.isMutating,
                    )
                }
            }
        }
    }
}

// 함께 보기에서 누른 칸에 가능한 사람 이름을 보여준다.
@Composable
private fun AvailabilityFocusedMembers(
    displayModel: EventAvailabilityDisplayModel,
    uiState: EventAvailabilityUiState,
) {
    val focusedCell = displayModel.cells.firstOrNull { cell -> cell.startTime == uiState.focusedSlotStart }
        ?: return
    val startLabel = focusedCell.startTime.toAvailabilityServerTime()
    val endLabel = focusedCell.endTime.toAvailabilityServerTime()
    val memberSeparator = stringResource(R.string.availability_member_separator)

    Text(
        text = if (focusedCell.availableMembers.isEmpty()) {
            stringResource(R.string.availability_members_none, startLabel, endLabel)
        } else {
            stringResource(
                R.string.availability_members_at,
                startLabel,
                endLabel,
                focusedCell.availableMembers.joinToString(memberSeparator) { member -> member.nickname },
            )
        },
        modifier = Modifier.padding(top = MoilAvailabilityDimension.SectionSpacing),
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
private fun AvailabilityTextAction(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .defaultMinSize(minHeight = MoilAuthDimension.ActionMinTouchTarget)
            .clickable(
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

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun EventAvailabilityScreenContentLoadingPreview() {
    MoilTheme(darkTheme = true) {
        EventAvailabilityScreenContent(
            uiState = EventAvailabilityUiState(
                eventId = 1L,
                eventTitle = "디자인 회의",
                date = LocalDate.of(2026, 9, 20),
            ),
            displayModel = EventAvailabilityDisplayModel(),
            onEvent = {},
        )
    }
}
