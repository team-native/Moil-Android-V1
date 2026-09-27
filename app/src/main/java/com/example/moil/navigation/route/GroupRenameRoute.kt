package com.example.moil.navigation.route

import androidx.compose.runtime.Composable
import com.example.moil.feature.family.view.FamilyGroupNameDialog
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState

/**
 * 선택한 그룹의 이름을 바꾸는 다이얼로그 목적지다.
 *
 * 저장 결과가 오기 전에는 닫지 않고, 성공 효과를 받은 `MoilMainUiStateEffects`가 다이얼로그를 닫는다.
 */
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
            val trimmedGroupName = updatedGroupName.trim()

            if (trimmedGroupName.isNotEmpty()) {
                groupViewModel.renameSelectedGroup(trimmedGroupName)
            }
        },
    )
}
