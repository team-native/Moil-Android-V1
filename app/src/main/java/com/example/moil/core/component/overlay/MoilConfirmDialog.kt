package com.example.moil.core.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilOverlayDimension
import com.example.moil.ui.theme.MoilRadius
import com.example.moil.ui.theme.MoilTheme

/** 확인 버튼의 강조 방식이다. 되돌릴 수 없는 동작은 [Destructive]로 primary와 구분한다. */
enum class MoilConfirmDialogTone {
    Default,
    Destructive,
}

/**
 * 제목·설명과 취소/확인 두 버튼으로 사용자의 결정을 받는 공용 확인 다이얼로그다.
 *
 * [isInProgress] 동안에는 요청이 끝나기 전에 닫히거나 중복 확인되지 않도록 두 버튼과 바깥 탭 닫기를 막는다.
 */
@Composable
fun MoilConfirmDialog(
    title: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    tone: MoilConfirmDialogTone = MoilConfirmDialogTone.Default,
    isInProgress: Boolean = false,
) {
    val confirmContainerColor = when (tone) {
        MoilConfirmDialogTone.Default -> MaterialTheme.colorScheme.primary
        MoilConfirmDialogTone.Destructive -> LocalMoilExtraColors.current.destructive
    }

    MoilOverlayDialog(
        onDismissRequest = {
            if (!isInProgress) {
                onDismissRequest()
            }
        },
    ) {
        Column(
            modifier = modifier.padding(MoilOverlayDimension.DialogContentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )

            if (description != null) {
                Spacer(modifier = Modifier.height(MoilOverlayDimension.DialogDescriptionTopSpacing))

                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(MoilOverlayDimension.DialogActionTopPadding))

            Row(horizontalArrangement = Arrangement.spacedBy(MoilOverlayDimension.DialogActionSpacing)) {
                OutlinedButton(
                    onClick = onDismissRequest,
                    enabled = !isInProgress,
                    modifier = Modifier
                        .weight(1f)
                        .height(MoilOverlayDimension.DialogActionHeight),
                    shape = RoundedCornerShape(MoilRadius.DialogButton),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    ),
                ) {
                    Text(text = stringResource(R.string.family_dialog_cancel))
                }

                Button(
                    onClick = onConfirm,
                    enabled = !isInProgress,
                    modifier = Modifier
                        .weight(1f)
                        .height(MoilOverlayDimension.DialogActionHeight),
                    shape = RoundedCornerShape(MoilRadius.DialogButton),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = confirmContainerColor,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    if (isInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(MoilOverlayDimension.DialogProgressSize),
                            strokeWidth = MoilOverlayDimension.DialogProgressStrokeWidth,
                        )
                    } else {
                        Text(text = confirmLabel)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MoilConfirmDialogPreview() {
    MoilTheme(darkTheme = true) {
        MoilConfirmDialog(
            title = stringResource(R.string.family_leave_last_member_title),
            description = stringResource(R.string.family_leave_confirm_description),
            confirmLabel = stringResource(R.string.family_leave_confirm_action),
            tone = MoilConfirmDialogTone.Destructive,
            onConfirm = {},
            onDismissRequest = {},
        )
    }
}
