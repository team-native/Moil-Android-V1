package com.example.moil.feature.profile.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.ui.theme.MoilProfileDimension

/** 마이페이지에서 현재 세션의 로컬 프로필을 표시하고 편집 화면 진입을 제공합니다. */
@Composable
internal fun ProfileAccountHeader(
    profileName: String,
    @DrawableRes profileAvatarRes: Int,
    onProfileImageClick: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(profileAvatarRes),
            contentDescription = stringResource(R.string.profile_edit_avatar_content_description),
            modifier = Modifier
                .size(MoilProfileDimension.AvatarSize)
                .clip(CircleShape)
                .clickable(onClick = onProfileImageClick),
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.width(MoilProfileDimension.HeaderContentSpacing))

        Text(
            text = profileName,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
