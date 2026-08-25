package com.example.moil.feature.profile.view

import androidx.compose.foundation.Image
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

@Composable
internal fun ProfileHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.family_avatar_member_green),
            contentDescription = stringResource(R.string.profile_name),
            modifier = Modifier
                .size(MoilProfileDimension.AvatarSize)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.width(MoilProfileDimension.HeaderContentSpacing))

        Text(
            text = stringResource(R.string.profile_name),
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
