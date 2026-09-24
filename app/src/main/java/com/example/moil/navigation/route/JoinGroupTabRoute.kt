package com.example.moil.navigation.route

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.example.moil.feature.group.view.JoinGroupScreen
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.feature.group.viewmodel.JoinGroupScreenEvent
import com.example.moil.feature.group.viewmodel.JoinGroupStep
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState
import com.example.moil.navigation.toMainDestination

/** JoinGroup 탭의 시작 화면이다. 초대 코드 확인과 가입 프로필 설정 두 단계를 한 화면에서 처리한다. */
@Composable
internal fun JoinGroupTabRoute(
    mainUiState: MoilMainUiState,
    groupViewModel: GroupViewModel,
    navigator: MoilMainNavigator,
) {
    val profileImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { selectedUri ->
        selectedUri?.let { uri ->
            mainUiState.joinGroupUiState = mainUiState.joinGroupUiState.copy(
                selectedProfileColor = null,
                selectedProfileImageUri = uri.toString(),
            )
        }
    }

    JoinGroupScreen(
        uiState = mainUiState.joinGroupUiState,
        onEvent = { event ->
            when (event) {
                is JoinGroupScreenEvent.DestinationClicked -> {
                    navigator.navigateToTab(event.destination.toMainDestination())
                }

                is JoinGroupScreenEvent.InviteCodeChanged -> {
                    mainUiState.joinGroupUiState = mainUiState.joinGroupUiState.copy(
                        inviteCode = event.inviteCode,
                    )
                }

                JoinGroupScreenEvent.InviteCodeConfirmed -> {
                    groupViewModel.verifyInvite(mainUiState.joinGroupUiState.inviteCode.trim())
                }

                JoinGroupScreenEvent.ProfileSetupBackClicked -> {
                    mainUiState.joinGroupUiState = mainUiState.joinGroupUiState.copy(
                        step = JoinGroupStep.InviteCode,
                    )
                }

                is JoinGroupScreenEvent.ProfileNameChanged -> {
                    mainUiState.joinGroupUiState = mainUiState.joinGroupUiState.copy(
                        profileName = event.profileName,
                    )
                }

                is JoinGroupScreenEvent.ProfileColorSelected -> {
                    mainUiState.joinGroupUiState = mainUiState.joinGroupUiState.copy(
                        selectedProfileColor = event.color,
                        selectedProfileImageUri = null,
                    )
                }

                JoinGroupScreenEvent.CustomProfileImageClicked -> profileImagePicker.launch("image/*")

                JoinGroupScreenEvent.JoinGroupConfirmed -> {
                    val joinGroupUiState = mainUiState.joinGroupUiState
                    val selectedProfileColor = joinGroupUiState.selectedProfileColor
                    val selectedProfileImageUri = joinGroupUiState.selectedProfileImageUri

                    if (selectedProfileColor != null || selectedProfileImageUri != null) {
                        groupViewModel.joinGroup(
                            inviteCode = joinGroupUiState.inviteCode.trim(),
                            nickname = joinGroupUiState.profileName.trim(),
                            color = selectedProfileColor,
                            selectedImageUri = selectedProfileImageUri,
                        )
                    }
                }
            }
        },
    )
}
