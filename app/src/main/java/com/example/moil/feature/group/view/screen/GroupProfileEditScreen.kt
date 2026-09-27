package com.example.moil.feature.group.view

import androidx.compose.runtime.Composable
import com.example.moil.feature.group.viewmodel.GroupProfileEditScreenEvent
import com.example.moil.feature.group.viewmodel.GroupProfileEditUiState

/** 그룹 안의 내 프로필(닉네임·색·사진) 변경 화면이다. */
@Composable
fun GroupProfileEditScreen(
    uiState: GroupProfileEditUiState,
    onEvent: (GroupProfileEditScreenEvent) -> Unit,
) {
    GroupProfileEditScreenContent(
        uiState = uiState,
        onEvent = onEvent,
    )
}
