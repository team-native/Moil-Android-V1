package com.example.moil.feature.group.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.display.MoilRemoteAvatar
import com.example.moil.feature.group.viewmodel.JoinGroupUsedProfileUiModel
import com.example.moil.feature.group.viewmodel.avatarResourceForGroupColor
import com.example.moil.ui.theme.MoilGroupCreateDimension

@Composable
internal fun UsedProfileList(profiles: List<JoinGroupUsedProfileUiModel>) {
    Column {
        Text(
            text = stringResource(R.string.group_join_used_profiles_label),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )

        Row(
            modifier = Modifier.padding(top = MoilGroupCreateDimension.HeaderTitleSpacing),
            horizontalArrangement = Arrangement.spacedBy(MoilGroupCreateDimension.ColorOptionSpacing),
        ) {
            profiles.forEach { profile ->
                Column(
                    modifier = Modifier.alpha(if (profile.isUsed) 0.35f else 1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    MoilRemoteAvatar(
                        imagePath = profile.imagePath,
                        fallbackAvatarRes = avatarResourceForGroupColor(profile.color),
                        contentDescription = profile.nickname,
                        size = MoilGroupCreateDimension.ProfileAvatarImageSize,
                    )

                    Text(
                        text = profile.nickname,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}
