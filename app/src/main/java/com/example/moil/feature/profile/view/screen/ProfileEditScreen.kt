package com.example.moil.feature.profile.view

import androidx.compose.runtime.Composable
import com.example.moil.feature.profile.viewmodel.ProfileEditScreenEvent
import com.example.moil.feature.profile.viewmodel.ProfileEditUiState

@Composable
fun ProfileEditScreen(
    uiState: ProfileEditUiState,
    onEvent: (ProfileEditScreenEvent) -> Unit,
) {
    ProfileEditScreenContent(
        uiState = uiState,
        onEvent = onEvent,
    )
}
