package com.example.moil.feature.profile.view

import androidx.compose.runtime.Composable
import com.example.moil.feature.profile.viewmodel.ChangePasswordScreenEvent
import com.example.moil.feature.profile.viewmodel.ChangePasswordUiState

/** 마이페이지 비밀번호 변경 화면이다. */
@Composable
fun ChangePasswordScreen(
    uiState: ChangePasswordUiState,
    onEvent: (ChangePasswordScreenEvent) -> Unit,
) {
    ChangePasswordScreenContent(
        uiState = uiState,
        onEvent = onEvent,
    )
}
