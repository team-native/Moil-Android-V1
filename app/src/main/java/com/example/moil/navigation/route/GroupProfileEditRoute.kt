package com.example.moil.navigation.route

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.example.moil.feature.group.view.GroupProfileEditScreen
import com.example.moil.feature.group.viewmodel.GroupProfileEditScreenEvent
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState

/**
 * Family 탭 back stack에 push되는 그룹 내 프로필 변경 화면이다.
 *
 * 입력값은 [MoilMainUiState.groupProfileEditUiState]가 보관하고, 저장 성공 시 `MoilMainUiStateEffects`가 화면을 닫는다.
 */
@Composable
internal fun GroupProfileEditRoute(
    mainUiState: MoilMainUiState,
    groupViewModel: GroupViewModel,
    navigator: MoilMainNavigator,
) {
    val profileImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { selectedUri ->
        selectedUri?.let { uri ->
            mainUiState.groupProfileEditUiState = mainUiState.groupProfileEditUiState.copy(
                selectedProfileColor = null,
                selectedProfileImageUri = uri.toString(),
            )
        }
    }

    GroupProfileEditScreen(
        uiState = mainUiState.groupProfileEditUiState,
        onEvent = { event ->
            when (event) {
                GroupProfileEditScreenEvent.BackClicked -> navigator.goBack()

                is GroupProfileEditScreenEvent.ProfileNameChanged -> {
                    mainUiState.groupProfileEditUiState = mainUiState.groupProfileEditUiState.copy(
                        profileName = event.profileName,
                    )
                }

                is GroupProfileEditScreenEvent.ProfileColorSelected -> {
                    mainUiState.groupProfileEditUiState = mainUiState.groupProfileEditUiState.copy(
                        selectedProfileColor = event.color,
                        selectedProfileImageUri = null,
                    )
                }

                GroupProfileEditScreenEvent.CustomProfileImageClicked -> profileImagePicker.launch("image/*")

                GroupProfileEditScreenEvent.SaveClicked -> {
                    val groupProfileEditUiState = mainUiState.groupProfileEditUiState
                    val groupId = groupProfileEditUiState.groupId

                    if (groupId != null && groupProfileEditUiState.canSave) {
                        groupViewModel.updateMyGroupProfile(
                            groupId = groupId,
                            nickname = groupProfileEditUiState.profileName.trim(),
                            color = groupProfileEditUiState.selectedProfileColor,
                            selectedImageUri = groupProfileEditUiState.selectedProfileImageUri,
                            currentImagePath = groupProfileEditUiState.currentImagePath,
                        )
                    }
                }
            }
        },
    )
}
