package com.example.moil.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.moil.ui.theme.MoilOverlayDimension
import com.example.moil.ui.theme.MoilRadius
import com.example.moil.ui.theme.LocalMoilExtraColors

@Composable
fun MoilOverlayDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        val dismissInteractionSource = remember { MutableInteractionSource() }
        val consumeInteractionSource = remember { MutableInteractionSource() }

        ApplyDialogWindowBackgroundBlur(
            blurRadius = MoilOverlayDimension.GroupNameDialogBackgroundBlur,
            dimAmount = 0f,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = dismissInteractionSource,
                    indication = null,
                    onClick = onDismissRequest,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = MoilOverlayDimension.DialogMaxWidth)
                    .fillMaxWidth()
                    .padding(horizontal = MoilOverlayDimension.DialogHorizontalPadding)
                    .clickable(
                        interactionSource = consumeInteractionSource,
                        indication = null,
                        onClick = {},
                    ),
                shape = RoundedCornerShape(MoilRadius.Dialog),
                color = LocalMoilExtraColors.current.overlaySurface,
                content = content,
            )
        }
    }
}
