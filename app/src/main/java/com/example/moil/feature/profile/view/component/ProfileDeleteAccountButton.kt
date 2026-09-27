package com.example.moil.feature.profile.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.example.moil.R
import com.example.moil.ui.theme.MoilAuthDimension

/**
 * 로그아웃 아래에 두는 회원 탈퇴 텍스트 버튼이다.
 * 되돌릴 수 없는 동작이라 실수로 누르지 않도록 로그아웃보다 눈에 덜 띄게 표시한다.
 */
@Composable
internal fun ProfileDeleteAccountButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(MoilAuthDimension.ActionMinTouchTarget)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.profile_delete_account),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
