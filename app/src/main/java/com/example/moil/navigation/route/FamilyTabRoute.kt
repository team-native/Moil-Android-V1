package com.example.moil.navigation.route

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.feature.family.view.MemberScreen
import com.example.moil.feature.family.viewmodel.FamilyScreenEvent
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.navigation.MoilMainDestination
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState
import com.example.moil.navigation.toMainDestination

/** Family 탭의 시작 화면이다. 그룹 구성원과 그룹 설정 오버레이 진입을 담당한다. */
@Composable
internal fun FamilyTabRoute(
    mainUiState: MoilMainUiState,
    groupViewModel: GroupViewModel,
    navigator: MoilMainNavigator,
) {
    val context = LocalContext.current
    val inviteCodeLabel = stringResource(R.string.family_group_invite_code_label)

    MemberScreen(
        uiState = mainUiState.familyUiState,
        onEvent = { event ->
            when (event) {
                FamilyScreenEvent.BackClicked -> navigator.goBack()

                FamilyScreenEvent.EmptyGroupJoinClicked -> {
                    navigator.navigateToTab(MoilMainDestination.JoinGroup)
                }

                FamilyScreenEvent.EmptyGroupCreateClicked -> {
                    navigator.push(MoilMainDestination.CreateGroup)
                }

                is FamilyScreenEvent.DestinationClicked -> {
                    navigator.navigateToTab(event.destination.toMainDestination())
                }

                is FamilyScreenEvent.GroupClicked -> {
                    groupViewModel.selectGroup(event.groupId.toLong())
                }

                is FamilyScreenEvent.NotificationsChanged -> {
                    mainUiState.familyUiState = mainUiState.familyUiState.copy(
                        notificationsEnabled = event.isEnabled,
                    )
                }

                FamilyScreenEvent.GroupNameChangeClicked -> {
                    navigator.push(MoilMainDestination.GroupRename)
                }

                FamilyScreenEvent.MemberPermissionsClicked -> {
                    navigator.push(MoilMainDestination.MemberPermissions)
                }

                FamilyScreenEvent.InviteCodeCopyClicked -> {
                    mainUiState.familyUiState.selectedGroup?.let { selectedGroup ->
                        val clipboardManager = context
                            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                        clipboardManager.setPrimaryClip(
                            ClipData.newPlainText(inviteCodeLabel, selectedGroup.inviteCode),
                        )
                    }
                }

                FamilyScreenEvent.InviteLinkShareClicked -> {
                    navigator.push(MoilMainDestination.InviteShare)
                }

                FamilyScreenEvent.AdministratorTransferClicked -> {
                    navigator.push(MoilMainDestination.AdministratorTransfer)
                }
            }
        },
    )
}
