package com.example.moil.core.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
internal fun MoilGroupAvatarStack(
    @DrawableRes groupMemberAvatarResources: List<Int>,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        groupMemberAvatarResources.forEachIndexed { avatarIndex, avatarResource ->
            Image(
                painter = painterResource(avatarResource),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            if (avatarIndex != groupMemberAvatarResources.lastIndex) {
                Spacer(modifier = Modifier.width((-4).dp))
            }
        }
    }
}
