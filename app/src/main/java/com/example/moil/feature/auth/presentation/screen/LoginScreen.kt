package com.example.moil.feature.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.ui.theme.MoilTheme

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEvent: (LoginScreenEvent) -> Unit,
) {
    LoginScreenContent(
        uiState = uiState,
        onEvent = onEvent,
    )
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun LoginScreenPreview() {
    MoilTheme(darkTheme = false) {
        LoginScreen(
            uiState = LoginUiState(),
            onEvent = {},
        )
    }
}
