package com.example.moil.feature.family.presentation

import androidx.compose.runtime.Composable

@Composable
fun FamilyScreen(
    uiState: FamilyUiState,
    onEvent: (FamilyScreenEvent) -> Unit,
) {
    FamilyScreenContent(
        uiState = uiState,
        onEvent = onEvent,
    )
}
