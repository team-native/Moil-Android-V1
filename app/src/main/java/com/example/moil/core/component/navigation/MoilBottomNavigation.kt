package com.example.moil.core.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.moil.R
import com.example.moil.ui.theme.MoilSpacing

enum class MoilNavigationDestination(
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val selectedIconRes: Int,
    @param:DrawableRes val unselectedIconRes: Int,
    val isBottomNavigationItem: Boolean = true,
) {
    Calendar(
        labelRes = R.string.calendar_tab,
        selectedIconRes = R.drawable.calendar_tab_selected,
        unselectedIconRes = R.drawable.calendar_tab_unselected,
    ),
    Family(
        labelRes = R.string.family_tab,
        selectedIconRes = R.drawable.family_tab_selected,
        unselectedIconRes = R.drawable.family_tab_unselected,
    ),
    GroupDetail(
        labelRes = R.string.family_tab,
        selectedIconRes = R.drawable.family_tab_selected,
        unselectedIconRes = R.drawable.family_tab_unselected,
        isBottomNavigationItem = false,
    ),
    JoinGroup(
        labelRes = R.string.group_join,
        selectedIconRes = R.drawable.common_add_selected,
        unselectedIconRes = R.drawable.common_add_unselected,
    ),
    Profile(
        labelRes = R.string.profile_tab,
        selectedIconRes = R.drawable.profile_tab_selected,
        unselectedIconRes = R.drawable.profile_tab_unselected,
    ),
    ProfileEdit(
        labelRes = R.string.profile_account,
        selectedIconRes = R.drawable.profile_tab_selected,
        unselectedIconRes = R.drawable.profile_tab_unselected,
        isBottomNavigationItem = false,
    ),
    CreateGroup(
        labelRes = R.string.group_create,
        selectedIconRes = R.drawable.common_add_selected,
        unselectedIconRes = R.drawable.common_add_unselected,
        isBottomNavigationItem = false,
    ),
}

@Composable
fun MoilBottomNavigation(
    selectedDestination: MoilNavigationDestination,
    onDestinationClick: (MoilNavigationDestination) -> Unit,
) {
    val bottomNavigationDividerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    Surface(color = MaterialTheme.colorScheme.background) {
        Column {
            HorizontalDivider(color = bottomNavigationDividerColor)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = MoilSpacing.BottomNavigationTop)
                    .padding(bottom = MoilSpacing.BottomNavigationBottom)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                MoilNavigationDestination.entries
                    .filter { destination -> destination.isBottomNavigationItem }
                    .forEach { destination ->
                    val isSelected = destination == selectedDestination
                    MoilBottomNavigationItem(
                        iconRes = if (isSelected) {
                            destination.selectedIconRes
                        } else {
                            destination.unselectedIconRes
                        },
                        labelRes = destination.labelRes,
                        onClick = { onDestinationClick(destination) },
                    )
                }
            }
        }
    }
}
