package com.example.moil.navigation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.moil.feature.family.view.FamilyScreen
import com.example.moil.feature.family.viewmodel.FamilyScreenEvent
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState

/**
 * Family 탭 back stack에 push되는 그룹 상세 화면이다.
 *
 * Navigation 3의 NavKey 생성자 값은 `SavedStateHandle`에 자동으로 채워지지 않으므로,
 * 선택 그룹은 Route가 [LaunchedEffect]로 ViewModel에 직접 전달한다.
 */
@Composable
internal fun GroupDetailRoute(
    groupId: Long,
    mainUiState: MoilMainUiState,
    groupViewModel: GroupViewModel,
    navigator: MoilMainNavigator,
) {
    LaunchedEffect(groupId) {
        groupViewModel.selectGroup(groupId)
    }

    FamilyScreen(
        uiState = mainUiState.familyUiState,
        onEvent = { event ->
            when (event) {
                FamilyScreenEvent.BackClicked -> navigator.goBack()
                else -> Unit
            }
        },
    )
}
