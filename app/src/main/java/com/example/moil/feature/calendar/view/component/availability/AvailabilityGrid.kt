package com.example.moil.feature.calendar.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.feature.calendar.viewmodel.AVAILABILITY_SLOT_MINUTES
import com.example.moil.feature.calendar.viewmodel.EventAvailabilityCellUiModel
import com.example.moil.feature.calendar.viewmodel.availabilityGridSlotStarts
import com.example.moil.feature.calendar.viewmodel.toAvailabilityServerTime
import com.example.moil.ui.theme.MoilAvailabilityDimension
import com.example.moil.ui.theme.MoilTheme
import java.time.LocalTime

/**
 * 30분 단위 가능 시간 격자다.
 *
 * - 함께 보기([selectedSlotStarts]가 null): 칸마다 가능 인원 수를 숫자로 적고 인원 비율만큼 primary를 진하게 칠한다.
 *   색만으로 정보를 전하지 않도록 숫자와 접근성 설명을 함께 제공한다.
 * - 내 시간 입력: 칸을 눌러 선택·해제한다.
 */
@Composable
internal fun AvailabilityGrid(
    cells: List<EventAvailabilityCellUiModel>,
    participantCount: Int,
    selectedSlotStarts: Set<LocalTime>?,
    focusedSlotStart: LocalTime?,
    isEnabled: Boolean,
    onSlotClick: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cellsByStart = cells.associateBy(EventAvailabilityCellUiModel::startTime)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MoilAvailabilityDimension.CellGap),
    ) {
        availabilityGridSlotStarts().forEach { slotStart ->
            val slotEnd = slotStart.plusMinutes(AVAILABILITY_SLOT_MINUTES)
            val cell = cellsByStart[slotStart]

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = slotStart.toAvailabilityServerTime(),
                    modifier = Modifier.width(MoilAvailabilityDimension.TimeLabelWidth),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )

                if (selectedSlotStarts == null) {
                    AvailabilityCountCell(
                        slotStart = slotStart,
                        slotEnd = slotEnd,
                        availableCount = cell?.availableCount ?: 0,
                        isAvailableForEveryone = cell?.isAvailableForEveryone == true,
                        participantCount = participantCount,
                        isFocused = focusedSlotStart == slotStart,
                        onClick = { onSlotClick(slotStart) },
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    AvailabilitySelectableCell(
                        slotStart = slotStart,
                        slotEnd = slotEnd,
                        isSelected = slotStart in selectedSlotStarts,
                        isEnabled = isEnabled,
                        onClick = { onSlotClick(slotStart) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun AvailabilityCountCell(
    slotStart: LocalTime,
    slotEnd: LocalTime,
    availableCount: Int,
    isAvailableForEveryone: Boolean,
    participantCount: Int,
    isFocused: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cellDescription = stringResource(
        R.string.availability_cell_description_count,
        slotStart.toAvailabilityServerTime(),
        slotEnd.toAvailabilityServerTime(),
        availableCount,
    )
    val heatAlpha = availabilityHeatAlpha(
        availableCount = availableCount,
        participantCount = participantCount,
    )
    val containerColor = if (availableCount == 0) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = heatAlpha)
    }
    val contentColor = if (isAvailableForEveryone) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = modifier
            .height(MoilAvailabilityDimension.SlotRowHeight)
            .availabilityFocusBorder(
                isFocused = isFocused,
                borderColor = MaterialTheme.colorScheme.onSurface,
            )
            .selectable(
                selected = isFocused,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { contentDescription = cellDescription },
        shape = RoundedCornerShape(MoilAvailabilityDimension.CellCornerRadius),
        color = containerColor,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (availableCount > 0) {
                Text(
                    text = if (isAvailableForEveryone) {
                        stringResource(R.string.availability_cell_everyone)
                    } else {
                        stringResource(R.string.availability_cell_count, availableCount)
                    },
                    color = contentColor,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun AvailabilitySelectableCell(
    slotStart: LocalTime,
    slotEnd: LocalTime,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cellDescription = stringResource(
        if (isSelected) {
            R.string.availability_cell_description_selected
        } else {
            R.string.availability_cell_description_unselected
        },
        slotStart.toAvailabilityServerTime(),
        slotEnd.toAvailabilityServerTime(),
    )

    Surface(
        modifier = modifier
            .height(MoilAvailabilityDimension.SlotRowHeight)
            .toggleable(
                value = isSelected,
                enabled = isEnabled,
                role = Role.Checkbox,
                onValueChange = { onClick() },
            )
            .semantics { contentDescription = cellDescription },
        shape = RoundedCornerShape(MoilAvailabilityDimension.CellCornerRadius),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
    ) {}
}

// 포커스된 칸(가능한 사람을 보고 있는 칸)을 테두리로 구분한다.
private fun Modifier.availabilityFocusBorder(
    isFocused: Boolean,
    borderColor: Color,
): Modifier = if (isFocused) {
    border(
        width = MoilAvailabilityDimension.CellBorderWidth,
        color = borderColor,
        shape = RoundedCornerShape(MoilAvailabilityDimension.CellCornerRadius),
    )
} else {
    this
}

// 가능 인원 비율을 4단계 진하기로 바꾼다. 액센트 색 하나만 써서 라이트·다크 모두에서 읽히게 한다.
private fun availabilityHeatAlpha(
    availableCount: Int,
    participantCount: Int,
): Float {
    if (availableCount == 0 || participantCount == 0) {
        return 0f
    }

    val availableRatio = availableCount.toFloat() / participantCount

    return when {
        availableRatio >= 1f -> HEAT_ALPHA_EVERYONE
        availableRatio > 0.5f -> HEAT_ALPHA_MOST
        availableRatio > 0.25f -> HEAT_ALPHA_SOME
        else -> HEAT_ALPHA_FEW
    }
}

private const val HEAT_ALPHA_FEW = 0.2f
private const val HEAT_ALPHA_SOME = 0.4f
private const val HEAT_ALPHA_MOST = 0.65f
private const val HEAT_ALPHA_EVERYONE = 1f

@Preview(showBackground = true, heightDp = 600)
@Composable
private fun AvailabilityGridPreview() {
    MoilTheme(darkTheme = true) {
        AvailabilityGrid(
            cells = availabilityGridSlotStarts().mapIndexed { index, slotStart ->
                EventAvailabilityCellUiModel(
                    startTime = slotStart,
                    endTime = slotStart.plusMinutes(AVAILABILITY_SLOT_MINUTES),
                    availableCount = index % 4,
                    isAvailableForEveryone = index % 4 == 3,
                    availableMembers = emptyList(),
                )
            },
            participantCount = 3,
            selectedSlotStarts = null,
            focusedSlotStart = null,
            isEnabled = true,
            onSlotClick = {},
        )
    }
}
