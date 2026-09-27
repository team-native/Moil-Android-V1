package com.example.moil.feature.auth.view

import androidx.compose.runtime.Composable
import com.example.moil.feature.auth.viewmodel.PasswordResetScreenEvent
import com.example.moil.feature.auth.viewmodel.PasswordResetStep
import com.example.moil.feature.auth.viewmodel.PasswordResetUiState

/** 비밀번호 찾기 화면. 현재 단계에 맞는 입력 화면을 보여준다. */
@Composable
fun PasswordResetScreen(
    uiState: PasswordResetUiState,
    onEvent: (PasswordResetScreenEvent) -> Unit,
) {
    when (uiState.currentStep) {
        PasswordResetStep.Email -> {
            PasswordResetEmailContent(
                uiState = uiState,
                onEvent = onEvent,
            )
        }

        PasswordResetStep.Verification -> {
            PasswordResetVerificationContent(
                uiState = uiState,
                onEvent = onEvent,
            )
        }

        PasswordResetStep.NewPassword -> {
            PasswordResetNewPasswordContent(
                uiState = uiState,
                onEvent = onEvent,
            )
        }
    }
}
