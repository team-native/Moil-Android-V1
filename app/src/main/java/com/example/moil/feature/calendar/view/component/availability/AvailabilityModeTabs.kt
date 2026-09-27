package com.example.moil.feature.calendar.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.example.moil.R
import com.example.moil.feature.calendar.viewmodel.EventAvailabilityTab
import com.example.moil.ui.theme.MoilAvailabilityDimension

/** 가능 시간 화면의 "함께 보기 / 내 시간 입력" 전환 탭이다. */
@Composable
internal fun AvailabilityModeTabs(
    selectedTab: EventAvailabilityTab,
    onTabSelected: (EventAvailabilityTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MoilAvailabilityDimension.CellGap),
    ) {
        EventAvailabilityTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(MoilAvailabilityDimension.TabHeight)
                    .selectable(
                        selected = isSelected,
                        role = Role.Tab,
                        onClick = { onTabSelected(tab) },
                    ),
                shape = RoundedCornerShape(MoilAvailabilityDimension.TabCornerRadius),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(
                            when (tab) {
                                EventAvailabilityTab.Together -> R.string.availability_tab_together
                                EventAvailabilityTab.Mine -> R.string.availability_tab_mine
                            },
                        ),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}
