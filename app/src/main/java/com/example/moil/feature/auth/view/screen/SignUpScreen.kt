package com.example.moil.feature.auth.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.feature.auth.viewmodel.SignUpScreenEvent
import com.example.moil.feature.auth.viewmodel.SignUpStep
import com.example.moil.feature.auth.viewmodel.SignUpUiState
import com.example.moil.ui.theme.MoilTheme

@Composable
fun SignUpScreen(
    uiState: SignUpUiState,
    onEvent: (SignUpScreenEvent) -> Unit,
) {
    SignUpStepContent(
        uiState = uiState,
        onEvent = onEvent,
    )
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun SignUpPasswordScreenPreview() {
    MoilTheme(darkTheme = false) {
        SignUpScreen(
            uiState = SignUpUiState(
                currentStep = SignUpStep.Password,
                password = "1234",
            ),
            onEvent = {},
        )
    }
}
