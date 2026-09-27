package com.example.moil.navigation.route

import androidx.compose.runtime.Composable
import com.example.moil.feature.family.view.FamilyMemberPermissionsBottomSheet
import com.example.moil.feature.family.viewmodel.toGroupRole
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.navigation.MoilMainUiState

/**
 * 그룹 구성원 권한을 조정하는 바텀시트 목적지다.
 *
 * 완료를 누르면 서버에 권한 변경을 요청하고, 성공 효과를 받은 `MoilMainUiStateEffects`가 시트를 닫는다.
 */
@Composable
internal fun MemberPermissionsRoute(
    mainUiState: MoilMainUiState,
    groupViewModel: GroupViewModel,
) {
    val familyUiState = mainUiState.familyUiState
    val selectedGroup = familyUiState.selectedGroup ?: return

    FamilyMemberPermissionsBottomSheet(
        members = selectedGroup.members,
        isInProgress = familyUiState.isManagementInProgress,
        onConfirmClick = { selectedRoles ->
            groupViewModel.updateSelectedMemberRoles(
                selectedRoles.mapValues { (_, selectedRole) -> selectedRole.toGroupRole() },
            )
        },
    )
}
