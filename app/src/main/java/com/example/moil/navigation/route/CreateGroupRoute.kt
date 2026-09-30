package com.example.moil.navigation.route

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moil.feature.group.view.CreateGroupScreen
import com.example.moil.feature.group.viewmodel.CreateGroupNameError
import com.example.moil.feature.group.viewmodel.CreateGroupNameValidator
import com.example.moil.feature.group.viewmodel.CreateGroupScreenEvent
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.feature.group.viewmodel.groupColorForAvatar
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState

/** 진입 시점의 활성 탭 back stack에 push되는 그룹 생성 화면이다. 뒤로가기는 그 탭의 이전 화면으로 돌아간다. */
@Composable
internal fun CreateGroupRoute(
    mainUiState: MoilMainUiState,
    groupViewModel: GroupViewModel,
    navigator: MoilMainNavigator,
) {
    val groupUiState by groupViewModel.uiState.collectAsStateWithLifecycle()
    val existingGroupNames = groupUiState.groups.map { group -> group.name }
    val profileImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { selectedUri ->
        selectedUri?.let { uri ->
            mainUiState.createGroupUiState = mainUiState.createGroupUiState.copy(
                selectedProfileAvatarRes = null,
                selectedProfileImageUri = uri.toString(),
            )
        }
    }

    CreateGroupScreen(
        uiState = mainUiState.createGroupUiState,
        onEvent = { event ->
            when (event) {
                CreateGroupScreenEvent.BackClicked -> navigator.goBack()

                is CreateGroupScreenEvent.GroupNameChanged -> {
                    mainUiState.createGroupUiState = mainUiState.createGroupUiState.copy(
                        groupName = event.groupName,
                        groupNameError = null,
                    )
                }

                is CreateGroupScreenEvent.ProfileAvatarSelected -> {
                    mainUiState.createGroupUiState = mainUiState.createGroupUiState.copy(
                        selectedProfileAvatarRes = event.avatarRes,
                        selectedProfileImageUri = null,
                    )
                }

                CreateGroupScreenEvent.CustomProfileImageClicked -> profileImagePicker.launch("image/*")

                CreateGroupScreenEvent.CreateGroupClicked -> {
                    val createGroupUiState = mainUiState.createGroupUiState
                    val normalizedGroupName = createGroupUiState.groupName.trim()
                    val isDuplicateGroupName = CreateGroupNameValidator.isDuplicate(
                        groupName = normalizedGroupName,
                        existingGroupNames = existingGroupNames,
                    )

                    if (isDuplicateGroupName) {
                        mainUiState.createGroupUiState = createGroupUiState.copy(
                            groupNameError = CreateGroupNameError.Duplicate,
                        )
                    } else {
                        groupViewModel.createGroup(
                            name = normalizedGroupName,
                            color = createGroupUiState.selectedProfileAvatarRes
                                ?.let(::groupColorForAvatar),
                            selectedImageUri = createGroupUiState.selectedProfileImageUri,
                        )
                    }
                }
            }
        },
    )
}
