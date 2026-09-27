package com.example.moil.navigation.route

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.MoilConfirmDialog
import com.example.moil.core.component.MoilConfirmDialogTone
import com.example.moil.feature.group.viewmodel.GroupLeavePolicy
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.navigation.MoilMainDestination
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState

/**
 * 그룹 나가기 확인 다이얼로그 목적지다.
 *
 * 혼자 남은 그룹이면 나가는 순간 그룹이 삭제된다는 경고 문구로 바꿔 보여준다.
 * 성공하면 `MoilMainUiStateEffects`가 GroupLeft 효과를 받아 이 다이얼로그와 그룹 상세를 닫는다.
 */
@Composable
internal fun LeaveGroupConfirmationRoute(
    mainUiState: MoilMainUiState,
    groupViewModel: GroupViewModel,
    navigator: MoilMainNavigator,
) {
    val familyUiState = mainUiState.familyUiState
    val selectedGroup = familyUiState.selectedGroup ?: return
    val isLastMember = familyUiState.leavePolicy == GroupLeavePolicy.LastMember

    MoilConfirmDialog(
        title = if (isLastMember) {
            stringResource(R.string.family_leave_last_member_title)
        } else {
            stringResource(R.string.family_leave_confirm_title, selectedGroup.name)
        },
        description = if (isLastMember) {
            stringResource(R.string.family_leave_last_member_description, selectedGroup.name)
        } else {
            stringResource(R.string.family_leave_confirm_description)
        },
        confirmLabel = if (isLastMember) {
            stringResource(R.string.family_leave_last_member_action)
        } else {
            stringResource(R.string.family_leave_confirm_action)
        },
        tone = MoilConfirmDialogTone.Destructive,
        isInProgress = familyUiState.isManagementInProgress,
        onConfirm = groupViewModel::leaveSelectedGroup,
        onDismissRequest = navigator::goBack,
    )
}

/** 나가기 버튼을 눌렀을 때 선택 그룹의 나가기 절차에 맞는 다이얼로그를 연다. */
internal fun MoilMainNavigator.openLeaveGroupFlow(leavePolicy: GroupLeavePolicy) {
    when (leavePolicy) {
        GroupLeavePolicy.TransferRequired -> {
            push(MoilMainDestination.LeaveGroupAdministratorTransfer)
        }

        GroupLeavePolicy.Confirm,
        GroupLeavePolicy.LastMember,
        -> {
            push(MoilMainDestination.LeaveGroupConfirmation)
        }
    }
}
