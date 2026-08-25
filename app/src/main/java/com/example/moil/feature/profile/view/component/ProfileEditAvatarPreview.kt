package com.example.moil.feature.profile.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.ui.theme.MoilProfileEditDimension

@Composable
internal fun ProfileEditAvatarPreview(@DrawableRes avatarRes: Int) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(avatarRes),
            contentDescription = stringResource(R.string.profile_edit_avatar_preview),
            modifier = Modifier
                .size(MoilProfileEditDimension.AvatarSize)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    }
}
