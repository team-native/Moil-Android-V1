package com.example.moil.core.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.moil.R
import com.example.moil.ui.theme.MoilSpacing

/**
 * 하단 탭바에 표시되는 항목의 UI 메타데이터다.
 *
 * 탐색 자체는 `MoilMainDestination`이 담당하며, 여기서는 라벨·아이콘만 정의한다.
 * 탭바에 나오지 않는 화면(그룹 상세, 프로필 편집, 그룹 생성)은 이 enum에 두지 않는다.
 */
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
}

@Composable
fun MoilBottomNavigation(
    selectedDestination: MoilNavigationDestination,
    onDestinationClick: (MoilNavigationDestination) -> Unit,
) {
    val bottomNavigationDividerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    Surface(color = MaterialTheme.colorScheme.background) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = MoilSpacing.BottomNavigationTop,
                        bottom = MoilSpacing.BottomNavigationBottom,
                    )
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

            HorizontalDivider(
                color = bottomNavigationDividerColor,
                modifier = Modifier.padding(top = MoilSpacing.BottomNavigationTop),
            )
        }
    }
}
