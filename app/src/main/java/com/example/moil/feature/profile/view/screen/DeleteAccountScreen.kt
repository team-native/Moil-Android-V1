package com.example.moil.feature.profile.view

import androidx.compose.runtime.Composable
import com.example.moil.feature.profile.viewmodel.DeleteAccountScreenEvent
import com.example.moil.feature.profile.viewmodel.DeleteAccountUiState

/** 마이페이지 회원 탈퇴 화면이다. */
@Composable
fun DeleteAccountScreen(
    uiState: DeleteAccountUiState,
    onEvent: (DeleteAccountScreenEvent) -> Unit,
) {
    DeleteAccountScreenContent(
        uiState = uiState,
        onEvent = onEvent,
    )
}
