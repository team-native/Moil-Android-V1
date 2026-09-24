package com.example.moil.navigation.route

import androidx.compose.runtime.Composable
import com.example.moil.feature.profile.view.ProfileEditScreen
import com.example.moil.feature.profile.viewmodel.ProfileEditScreenEvent
import com.example.moil.navigation.MainTabViewModel
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState

/** Profile 탭 back stack에 push되는 프로필 편집 화면이다. */
@Composable
internal fun ProfileEditRoute(
    mainUiState: MoilMainUiState,
    mainTabViewModel: MainTabViewModel,
    navigator: MoilMainNavigator,
) {
    ProfileEditScreen(
        uiState = mainUiState.profileEditUiState,
        onEvent = { event ->
            when (event) {
                ProfileEditScreenEvent.BackClicked -> navigator.goBack()

                is ProfileEditScreenEvent.NameChanged -> {
                    mainTabViewModel.clearProfileSaveError()
                    mainUiState.profileEditUiState = mainUiState.profileEditUiState.copy(
                        profileName = event.profileName,
                        saveError = null,
                    )
                }

                is ProfileEditScreenEvent.ProfileAvatarSelected -> {
                    mainTabViewModel.clearProfileSaveError()
                    mainUiState.profileEditUiState = mainUiState.profileEditUiState.copy(
                        selectedProfileAvatarRes = event.avatarRes,
                        saveError = null,
                    )
                }

                ProfileEditScreenEvent.SaveClicked -> {
                    if (mainUiState.profileEditUiState.canSave) {
                        mainTabViewModel.updateProfileName(
                            mainUiState.profileEditUiState.profileName.trim(),
                        )
                    }
                }
            }
        },
    )
}
