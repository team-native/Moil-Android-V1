package com.example.moil.feature.profile.presentation

import androidx.compose.runtime.Composable

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
