package com.example.moil.navigation.route

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.MoilConfirmDialog
import com.example.moil.core.component.MoilConfirmDialogTone
import com.example.moil.feature.profile.view.DeleteAccountScreen
import com.example.moil.feature.profile.viewmodel.DeleteAccountScreenEvent
import com.example.moil.navigation.MainTabViewModel
import com.example.moil.navigation.MoilMainDestination
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState

/**
 * Profile 탭 back stack에 push되는 회원 탈퇴 화면이다.
 *
 * 탈퇴하기를 누르면 되돌릴 수 없는 동작이므로 확인 다이얼로그 목적지를 한 번 더 띄운다.
 */
@Composable
internal fun DeleteAccountRoute(
    mainUiState: MoilMainUiState,
    mainTabViewModel: MainTabViewModel,
    navigator: MoilMainNavigator,
) {
    DeleteAccountScreen(
        uiState = mainUiState.deleteAccountUiState,
        onEvent = { event ->
            when (event) {
                DeleteAccountScreenEvent.BackClicked -> navigator.goBack()

                is DeleteAccountScreenEvent.EmailChanged -> {
                    mainTabViewModel.clearAccountActionErrors()
                    mainUiState.deleteAccountUiState = mainUiState.deleteAccountUiState.copy(
                        email = event.email,
                    )
                }

                is DeleteAccountScreenEvent.PasswordChanged -> {
                    mainTabViewModel.clearAccountActionErrors()
                    mainUiState.deleteAccountUiState = mainUiState.deleteAccountUiState.copy(
                        password = event.password,
                    )
                }

                is DeleteAccountScreenEvent.KeepSchedulesChanged -> {
                    mainUiState.deleteAccountUiState = mainUiState.deleteAccountUiState.copy(
                        shouldKeepSchedules = event.shouldKeepSchedules,
                    )
                }

                DeleteAccountScreenEvent.SubmitClicked -> {
                    if (mainUiState.deleteAccountUiState.canSubmit) {
                        navigator.push(MoilMainDestination.DeleteAccountConfirmation)
                    }
                }
            }
        },
    )
}

/**
 * 회원 탈퇴 최종 확인 다이얼로그 목적지다.
 *
 * 확인하면 다이얼로그를 닫고 탈퇴를 요청한다. 진행 상태와 실패 사유는 탈퇴 화면에 표시되고,
 * 성공하면 세션이 종료되어 앱이 로그인 화면으로 돌아간다.
 */
@Composable
internal fun DeleteAccountConfirmationRoute(
    mainUiState: MoilMainUiState,
    mainTabViewModel: MainTabViewModel,
    navigator: MoilMainNavigator,
) {
    MoilConfirmDialog(
        title = stringResource(R.string.account_delete_dialog_title),
        description = stringResource(R.string.account_delete_dialog_description),
        confirmLabel = stringResource(R.string.account_delete_dialog_action),
        tone = MoilConfirmDialogTone.Destructive,
        onConfirm = {
            val deleteAccountUiState = mainUiState.deleteAccountUiState

            navigator.goBack()
            mainTabViewModel.deleteAccount(
                email = deleteAccountUiState.email.trim(),
                password = deleteAccountUiState.password,
                shouldKeepSchedules = deleteAccountUiState.shouldKeepSchedules,
            )
        },
        onDismissRequest = navigator::goBack,
    )
}
