package com.example.moil.core.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

@Composable
fun MoilImageButton(
    @DrawableRes imageRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
        IconButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = contentDescription,
                modifier = imageModifier,
            )
        }
    }
}
