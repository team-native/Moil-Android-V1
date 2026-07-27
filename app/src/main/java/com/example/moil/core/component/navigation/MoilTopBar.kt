package com.example.moil.core.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.ui.theme.MoilTheme

@Composable
fun MoilTopBar(
    groupName: String,
    groupMenuContentDescription: String,
    @DrawableRes groupMemberAvatarResources: List<Int>,
    isGroupIndicatorExpanded: Boolean,
    searchContentDescription: String,
    onGroupClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MoilGroupSelector(
            groupName = groupName,
            groupMenuContentDescription = groupMenuContentDescription,
            groupMemberAvatarResources = groupMemberAvatarResources,
            isGroupIndicatorExpanded = isGroupIndicatorExpanded,
            onClick = onGroupClick,
        )

        Spacer(modifier = Modifier.weight(1f))

        MoilSearchButton(
            contentDescription = searchContentDescription,
            onClick = onSearchClick,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MoilTopBarPreview() {
    MoilTheme(darkTheme = false) {
        MoilTopBar(
            groupName = stringResource(R.string.calendar_family_name),
            groupMenuContentDescription = stringResource(R.string.calendar_group_menu),
            groupMemberAvatarResources = listOf(
                R.drawable.family_avatar_mine,
                R.drawable.family_avatar_mom,
                R.drawable.family_avatar_dad,
                R.drawable.family_avatar_sibling,
            ),
            isGroupIndicatorExpanded = false,
            searchContentDescription = stringResource(R.string.calendar_search),
            onGroupClick = {},
            onSearchClick = {},
        )
    }
}
