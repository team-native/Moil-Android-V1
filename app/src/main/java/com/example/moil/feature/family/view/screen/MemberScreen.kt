package com.example.moil.feature.family.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.feature.family.viewmodel.FamilyScreenEvent
import com.example.moil.feature.family.viewmodel.FamilyUiState
import com.example.moil.ui.theme.MoilTheme

@Composable
fun MemberScreen(
    uiState: FamilyUiState,
    onEvent: (FamilyScreenEvent) -> Unit,
) {
    MemberScreenContent(
        uiState = uiState,
        onEvent = onEvent,
    )
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun MemberScreenPreview() {
    MoilTheme(darkTheme = false) {
        MemberScreen(
            uiState = FamilyUiState(),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun MemberScreenEmptyGroupPreview() {
    MoilTheme(darkTheme = false) {
        MemberScreen(
            uiState = FamilyUiState(),
            onEvent = {},
        )
    }
}
