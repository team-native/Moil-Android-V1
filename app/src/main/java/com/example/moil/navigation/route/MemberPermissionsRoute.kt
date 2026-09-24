package com.example.moil.navigation.route

import androidx.compose.runtime.Composable
import com.example.moil.feature.family.view.FamilyMemberPermissionsBottomSheet
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState

/** 그룹 구성원 권한을 조정하는 바텀시트 목적지다. */
@Composable
internal fun MemberPermissionsRoute(
    mainUiState: MoilMainUiState,
    navigator: MoilMainNavigator,
) {
    val selectedGroup = mainUiState.familyUiState.selectedGroup ?: return

    FamilyMemberPermissionsBottomSheet(
        members = selectedGroup.members,
        onConfirmClick = { memberRoleOverrides ->
            mainUiState.familyUiState = mainUiState.familyUiState.copy(
                memberRoleOverrides = memberRoleOverrides,
            )
            navigator.goBack()
        },
    )
}
