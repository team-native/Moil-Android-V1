package com.example.moil.feature.profile.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.moil.ui.theme.MoilOverlayDimension
import com.example.moil.ui.theme.MoilProfileEditDimension
import com.example.moil.ui.theme.MoilTextFieldDimension

/**
 * 계정 화면 하단의 제출 버튼이다. 요청 중에는 진행 표시를 보여주고 중복 제출을 막는다.
 * 회원 탈퇴처럼 되돌릴 수 없는 동작은 [containerColor]로 강조색을 바꿔 쓴다.
 */
@Composable
internal fun AccountSubmitButton(
    text: String,
    enabled: Boolean,
    isInProgress: Boolean,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primary,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isInProgress,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = MoilProfileEditDimension.SaveButtonBottomPadding)
            .height(MoilProfileEditDimension.SaveButtonHeight),
        shape = RoundedCornerShape(MoilTextFieldDimension.CornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        if (isInProgress) {
            CircularProgressIndicator(
                modifier = Modifier.size(MoilOverlayDimension.DialogProgressSize),
                strokeWidth = MoilOverlayDimension.DialogProgressStrokeWidth,
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
