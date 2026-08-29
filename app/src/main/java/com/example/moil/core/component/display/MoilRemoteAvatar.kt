package com.example.moil.core.component.display

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.example.moil.core.network.RemoteImageUrlResolver

@Composable
fun MoilRemoteAvatar(
    imagePath: String?,
    @DrawableRes fallbackAvatarRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp,
) {
    val imageUrl = remember(imagePath) {
        RemoteImageUrlResolver.resolve(imagePath)
    }
    val fallbackPainter = painterResource(fallbackAvatarRes)

    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        placeholder = fallbackPainter,
        error = fallbackPainter,
        fallback = fallbackPainter,
    )
}
