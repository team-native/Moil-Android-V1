package com.example.moil.core.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.ui.theme.MoilRadius
import com.example.moil.ui.theme.MoilSpacing

enum class MoilNavigationDestination(
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val selectedIconRes: Int,
    @param:DrawableRes val unselectedIconRes: Int,
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
    AddSchedule(
        labelRes = R.string.add_schedule,
        selectedIconRes = R.drawable.common_add_selected,
        unselectedIconRes = R.drawable.common_add_unselected,
    ),
    Profile(
        labelRes = R.string.profile_tab,
        selectedIconRes = R.drawable.profile_tab_selected,
        unselectedIconRes = R.drawable.profile_tab_unselected,
    ),
}

@Composable
fun MoilBottomNavigation(
    selectedDestination: MoilNavigationDestination,
    onDestinationClick: (MoilNavigationDestination) -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MoilSpacing.BottomNavigationTop)
                    .padding(bottom = 10.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                MoilNavigationDestination.entries.forEach { destination ->
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

@Composable
private fun MoilBottomNavigationItem(
    @DrawableRes iconRes: Int,
    @StringRes labelRes: Int,
    onClick: () -> Unit,
) {
    val label = stringResource(labelRes)

    Image(
        painter = painterResource(iconRes),
        contentDescription = null,
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(MoilRadius.Event))
            .clickable(
                role = Role.Tab,
                onClick = onClick,
            )
            .semantics { contentDescription = label }
            .padding(12.dp),
        contentScale = ContentScale.Fit,
    )
}
