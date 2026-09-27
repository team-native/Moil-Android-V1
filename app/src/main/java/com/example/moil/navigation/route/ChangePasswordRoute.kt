package com.example.moil.navigation.route

import androidx.compose.runtime.Composable
import com.example.moil.feature.profile.view.ChangePasswordScreen
import com.example.moil.feature.profile.viewmodel.ChangePasswordScreenEvent
import com.example.moil.navigation.MainTabViewModel
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState

/**
 * Profile 탭 back stack에 push되는 비밀번호 변경 화면이다.
 *
 * 입력값은 [MoilMainUiState.changePasswordUiState]가 보관하고, 저장 성공 시 `MoilMainUiStateEffects`가 화면을 닫는다.
 */
@Composable
internal fun ChangePasswordRoute(
    mainUiState: MoilMainUiState,
    mainTabViewModel: MainTabViewModel,
    navigator: MoilMainNavigator,
) {
    ChangePasswordScreen(
        uiState = mainUiState.changePasswordUiState,
        onEvent = { event ->
            when (event) {
                ChangePasswordScreenEvent.BackClicked -> navigator.goBack()

                is ChangePasswordScreenEvent.CurrentPasswordChanged -> {
                    mainTabViewModel.clearAccountActionErrors()
                    mainUiState.changePasswordUiState = mainUiState.changePasswordUiState.copy(
                        currentPassword = event.password,
                    )
                }

                is ChangePasswordScreenEvent.NewPasswordChanged -> {
                    mainTabViewModel.clearAccountActionErrors()
                    mainUiState.changePasswordUiState = mainUiState.changePasswordUiState.copy(
                        newPassword = event.password,
                    )
                }

                is ChangePasswordScreenEvent.NewPasswordConfirmationChanged -> {
                    mainTabViewModel.clearAccountActionErrors()
                    mainUiState.changePasswordUiState = mainUiState.changePasswordUiState.copy(
                        newPasswordConfirmation = event.password,
                    )
                }

                ChangePasswordScreenEvent.SaveClicked -> {
                    val changePasswordUiState = mainUiState.changePasswordUiState

                    if (changePasswordUiState.canSave) {
                        mainTabViewModel.changePassword(
                            currentPassword = changePasswordUiState.currentPassword,
                            newPassword = changePasswordUiState.newPassword,
                            newPasswordConfirmation = changePasswordUiState.newPasswordConfirmation,
                        )
                    }
                }
            }
        },
    )
}
