package com.example.moil.navigation.route

import androidx.compose.runtime.Composable
import com.example.moil.feature.family.view.FamilyGroupNameDialog
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState

/** 선택한 그룹의 이름을 바꾸는 다이얼로그 목적지다. */
@Composable
internal fun GroupRenameRoute(
    mainUiState: MoilMainUiState,
    groupViewModel: GroupViewModel,
    navigator: MoilMainNavigator,
) {
    val selectedGroup = mainUiState.familyUiState.selectedGroup ?: return

    FamilyGroupNameDialog(
        groupName = selectedGroup.name,
        onDismissRequest = navigator::goBack,
        onSaveClick = { updatedGroupName ->
            groupViewModel.renameSelectedGroup(updatedGroupName.trim())
            navigator.goBack()
        },
    )
}
