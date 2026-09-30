package com.example.moil.navigation.route

import androidx.compose.runtime.Composable
import com.example.moil.feature.family.view.FamilyAdministratorTransferDialog
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState

/** 관리자 권한을 다른 구성원에게 넘기는 다이얼로그 목적지다. */
@Composable
internal fun AdministratorTransferRoute(
    mainUiState: MoilMainUiState,
    groupViewModel: GroupViewModel,
    navigator: MoilMainNavigator,
) {
    val selectedGroup = mainUiState.familyUiState.selectedGroup ?: return

    FamilyAdministratorTransferDialog(
        members = selectedGroup.members,
        onDismissRequest = navigator::goBack,
        onConfirmClick = { selectedMember ->
            groupViewModel.transferAdmin(selectedMember.id)
            navigator.goBack()
        },
    )
}
