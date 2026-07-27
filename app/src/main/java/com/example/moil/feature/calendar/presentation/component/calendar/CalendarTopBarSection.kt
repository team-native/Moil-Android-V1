package com.example.moil.feature.calendar.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.MoilTopBar
import com.example.moil.ui.theme.MoilComponentSize
import com.example.moil.ui.theme.MoilSpacing

@Composable
internal fun CalendarTopBarSection(
    isGroupMenuVisible: Boolean,
    isScheduleSheetVisible: Boolean,
    onGroupClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        MoilTopBar(
            groupName = stringResource(R.string.calendar_family_name),
            groupMenuContentDescription = stringResource(R.string.calendar_group_menu),
            groupMemberAvatarResources = calendarFamilyMemberAvatarResources,
            isGroupIndicatorExpanded = isGroupMenuVisible || isScheduleSheetVisible,
            searchContentDescription = stringResource(R.string.calendar_search),
            onGroupClick = onGroupClick,
            onSearchClick = {},
            modifier = Modifier.padding(top = MoilSpacing.HeaderTop),
        )

        if (isGroupMenuVisible) {
            CalendarGroupMenu(
                modifier = Modifier.padding(
                    top = MoilSpacing.HeaderTop + MoilComponentSize.TopBarItem,
                ),
            )
        }
    }
}
