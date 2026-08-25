package com.example.moil.feature.family.view

import androidx.compose.runtime.Composable
import com.example.moil.feature.family.viewmodel.FamilyScreenEvent
import com.example.moil.feature.family.viewmodel.FamilyUiState

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
