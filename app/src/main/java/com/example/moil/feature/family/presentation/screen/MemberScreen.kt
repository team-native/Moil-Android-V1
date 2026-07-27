package com.example.moil.feature.family.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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
