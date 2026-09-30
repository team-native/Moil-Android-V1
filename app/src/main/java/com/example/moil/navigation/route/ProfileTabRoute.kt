package com.example.moil.navigation.route

import androidx.compose.runtime.Composable
import com.example.moil.feature.profile.view.ProfileScreen
import com.example.moil.feature.profile.viewmodel.ProfileGroupIndicator
import com.example.moil.feature.profile.viewmodel.ProfileGroupUiModel
import com.example.moil.feature.profile.viewmodel.ProfileScreenEvent
import com.example.moil.feature.profile.viewmodel.toProfileEditUiState
import com.example.moil.navigation.MainTabViewModel
import com.example.moil.navigation.MoilMainDestination
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState
import com.example.moil.navigation.toMainDestination

/** Profile 탭의 시작 화면이다. */
@Composable
internal fun ProfileTabRoute(
    mainUiState: MoilMainUiState,
    mainTabViewModel: MainTabViewModel,
    navigator: MoilMainNavigator,
    onDarkThemeChanged: (Boolean) -> Unit,
) {
    val profileGroups = mainUiState.familyUiState.groups.mapIndexed { index, group ->
        ProfileGroupUiModel(
            id = group.id,
            name = group.name,
            indicator = if (index == 0) {
                ProfileGroupIndicator.Primary
            } else {
                ProfileGroupIndicator.Secondary
            },
        )
    }

    ProfileScreen(
        uiState = mainUiState.profileUiState,
        groups = profileGroups,
        onEvent = { event ->
            when (event) {
                is ProfileScreenEvent.DestinationClicked -> {
                    navigator.navigateToTab(event.destination.toMainDestination())
                }

                is ProfileScreenEvent.GroupClicked -> {
                    // 그룹 상세는 Family 탭에 속한 화면이므로, 탭을 먼저 옮긴 뒤 그 위에 얹어
                    // 뒤로가기가 상세 → Family 탭 시작 화면 순서로 자연스럽게 쌓이게 한다.
                    navigator.navigateToTabAndPush(
                        tab = MoilMainDestination.Family,
                        destination = MoilMainDestination.GroupDetail(event.groupId.toLong()),
                    )
                }

                is ProfileScreenEvent.DarkThemeChanged -> {
                    mainUiState.profileUiState = mainUiState.profileUiState.copy(
                        isDarkTheme = event.isDarkTheme,
                    )
                    onDarkThemeChanged(event.isDarkTheme)
                }

                ProfileScreenEvent.CreateGroupClicked -> {
                    navigator.push(MoilMainDestination.CreateGroup)
                }

                ProfileScreenEvent.ProfileImageClicked -> {
                    mainUiState.profileEditUiState = mainUiState.profileUiState.toProfileEditUiState()
                    navigator.push(MoilMainDestination.ProfileEdit)
                }

                ProfileScreenEvent.LogoutClicked -> mainTabViewModel.logout()
            }
        },
    )
}
