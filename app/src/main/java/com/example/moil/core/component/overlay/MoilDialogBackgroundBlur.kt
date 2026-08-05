package com.example.moil.core.component

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogWindowProvider
import kotlin.math.roundToInt

@Composable
fun ApplyDialogWindowBackgroundBlur(
    blurRadius: Dp,
    dimAmount: Float? = null,
) {
    val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
    val blurRadiusPx = with(LocalDensity.current) {
        blurRadius.toPx().roundToInt()
    }

    DisposableEffect(dialogWindow, blurRadiusPx, dimAmount) {
        dialogWindow?.apply {
            if (dimAmount != null) {
                setDimAmount(dimAmount)
            }

            addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            setBackgroundBlurRadius(blurRadiusPx)
        }

        onDispose {
            dialogWindow?.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                setBackgroundBlurRadius(NO_BACKGROUND_BLUR_RADIUS)
            }
        }
    }
}

fun Modifier.applyDialogBackdropBlur(
    shouldBlur: Boolean,
    blurRadius: Dp,
): Modifier = if (shouldBlur) {
    blur(blurRadius)
} else {
    this
}

private const val NO_BACKGROUND_BLUR_RADIUS = 0
