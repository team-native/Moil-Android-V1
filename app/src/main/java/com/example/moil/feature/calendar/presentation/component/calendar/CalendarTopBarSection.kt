package com.example.moil.feature.calendar.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.MoilTopBar
import com.example.moil.feature.calendar.presentation.CalendarGroupUiModel
import com.example.moil.ui.theme.MoilComponentSize
import com.example.moil.ui.theme.MoilSpacing

@Composable
internal fun CalendarTopBarSection(
    selectedGroupName: String,
    memberAvatarResources: List<Int>,
    groups: List<CalendarGroupUiModel>,
    selectedGroupId: Long?,
    isGroupMenuVisible: Boolean,
    isScheduleSheetVisible: Boolean,
    onGroupClick: () -> Unit,
    onGroupSelected: (Long) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        MoilTopBar(
            groupName = selectedGroupName,
            groupMenuContentDescription = stringResource(R.string.calendar_group_menu),
            groupMemberAvatarResources = memberAvatarResources,
            isGroupIndicatorExpanded = isGroupMenuVisible || isScheduleSheetVisible,
            searchContentDescription = stringResource(R.string.calendar_search),
            onGroupClick = onGroupClick,
            onSearchClick = {},
            modifier = Modifier.padding(top = MoilSpacing.HeaderTop),
        )

        if (isGroupMenuVisible) {
            CalendarGroupMenu(
                groups = groups,
                selectedGroupId = selectedGroupId,
                onGroupClick = onGroupSelected,
                modifier = Modifier.padding(
                    top = MoilSpacing.HeaderTop + MoilComponentSize.TopBarItem,
                ),
            )
        }
    }
}
