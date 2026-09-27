package com.example.moil.navigation.route

import androidx.compose.runtime.Composable
import com.example.moil.R
import com.example.moil.feature.family.view.FamilyAdministratorTransferDialog
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState

/**
 * 유일한 관리자가 그룹을 나가기 전에 관리자 권한을 넘길 멤버를 고르는 다이얼로그 목적지다.
 *
 * 요청 결과가 오기 전에는 닫지 않는다. 성공하면 [com.example.moil.feature.group.viewmodel.GroupEffect.GroupLeft]
 * 효과를 받은 `MoilMainUiStateEffects`가 이 다이얼로그를 닫는다.
 */
@Composable
internal fun LeaveGroupAdministratorTransferRoute(
    mainUiState: MoilMainUiState,
    groupViewModel: GroupViewModel,
    navigator: MoilMainNavigator,
) {
    val familyUiState = mainUiState.familyUiState
    val selectedGroup = familyUiState.selectedGroup ?: return

    FamilyAdministratorTransferDialog(
        members = selectedGroup.members,
        descriptionRes = R.string.family_admin_transfer_leave_description,
        confirmLabelRes = R.string.family_admin_transfer_leave_confirm,
        isInProgress = familyUiState.isManagementInProgress,
        onDismissRequest = navigator::goBack,
        onConfirmClick = { selectedMember ->
            groupViewModel.transferAdminAndLeaveSelectedGroup(selectedMember.id)
        },
    )
}
