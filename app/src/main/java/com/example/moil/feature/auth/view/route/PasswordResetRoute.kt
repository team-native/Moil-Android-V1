package com.example.moil.feature.auth.view

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moil.feature.auth.viewmodel.PasswordResetEffect
import com.example.moil.feature.auth.viewmodel.PasswordResetScreenEvent
import com.example.moil.feature.auth.viewmodel.PasswordResetStep
import com.example.moil.feature.auth.viewmodel.PasswordResetViewModel

/**
 * 로그인 전 비밀번호 찾기 목적지다.
 *
 * 단계 전환은 ViewModel 상태로 처리하고, 첫 단계에서 뒤로 가거나 변경을 마치면 로그인 화면으로 돌아간다.
 */
@Composable
fun PasswordResetRoute(
    initialEmail: String,
    onNavigateBack: () -> Unit,
    onPasswordResetCompleted: (String) -> Unit,
    viewModel: PasswordResetViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(initialEmail) {
        viewModel.prefillEmail(initialEmail)
    }

    // 시스템 뒤로가기도 단계형 흐름을 한 단계씩 되돌리도록 첫 단계가 아닐 때만 가로챈다.
    BackHandler(enabled = uiState.currentStep != PasswordResetStep.Email) {
        viewModel.onEvent(PasswordResetScreenEvent.BackClicked)
    }

    PasswordResetScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PasswordResetEffect.Completed -> onPasswordResetCompleted(effect.email)
                PasswordResetEffect.ExitRequested -> onNavigateBack()
            }
        }
    }
}
