package com.example.moil.feature.group.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import coil.compose.AsyncImage
import com.example.moil.R
import com.example.moil.ui.theme.MoilGroupCreateDimension

@Composable
internal fun ProfileAvatarSelector(
    @androidx.annotation.StringRes labelRes: Int = R.string.group_profile_avatar_label,
    selectedProfileAvatarRes: Int?,
    selectedProfileImageUri: String? = null,
    onProfileAvatarSelected: (Int) -> Unit,
    onCustomProfileImageClick: () -> Unit = {},
    showCustomProfileImage: Boolean = true,
    avatarResources: List<Int> = profileAvatarResources,
) {
    Column {
        Text(
            text = stringResource(labelRes),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )

        Row(
            modifier = Modifier
                .padding(top = MoilGroupCreateDimension.HeaderTitleSpacing)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(MoilGroupCreateDimension.ColorOptionSpacing),
        ) {
            avatarResources.forEachIndexed { index, avatarRes ->
                ProfileAvatarOption(
                    avatarRes = avatarRes,
                    isSelected = selectedProfileAvatarRes == avatarRes,
                    contentDescription = stringResource(
                        R.string.group_profile_avatar_option,
                        index + 1,
                    ),
                    onClick = { onProfileAvatarSelected(avatarRes) },
                )
            }

            if (showCustomProfileImage) {
                AddProfileAvatarOption(
                    selectedProfileImageUri = selectedProfileImageUri,
                    onClick = onCustomProfileImageClick,
                )
            }
        }
    }
}

@Composable
private fun ProfileAvatarOption(
    @androidx.annotation.DrawableRes
    avatarRes: Int,
    isSelected: Boolean,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(MoilGroupCreateDimension.ProfileAvatarTouchTargetSize)
            .then(
                if (isSelected) {
                    Modifier
                        .border(
                            width = MoilGroupCreateDimension.SelectedProfileAvatarOuterBorder,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                        )
                        .padding(MoilGroupCreateDimension.SelectedProfileAvatarInnerGap)
                        .border(
                            width = MoilGroupCreateDimension.SelectedProfileAvatarInnerGap,
                            color = MaterialTheme.colorScheme.background,
                            shape = CircleShape,
                        )
                } else {
                    Modifier
                },
            )
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(avatarRes),
            contentDescription = null,
            modifier = Modifier
                .size(MoilGroupCreateDimension.ProfileAvatarImageSize)
                .clip(CircleShape),
        )
    }
}

@Composable
private fun AddProfileAvatarOption(
    selectedProfileImageUri: String?,
    onClick: () -> Unit,
) {
    val addProfileAvatarContentDescription = stringResource(R.string.group_profile_avatar_add)

    Box(
        modifier = Modifier
            .size(MoilGroupCreateDimension.ProfileAvatarTouchTargetSize)
            .then(
                if (selectedProfileImageUri != null) {
                    Modifier.border(
                        width = MoilGroupCreateDimension.SelectedProfileAvatarOuterBorder,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                    )
                } else {
                    Modifier
                },
            )
            .selectable(
                selected = selectedProfileImageUri != null,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .semantics { this.contentDescription = addProfileAvatarContentDescription },
        contentAlignment = Alignment.Center,
    ) {
        if (selectedProfileImageUri == null) {
            Image(
                painter = painterResource(R.drawable.group_profile_avatar_add),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            AsyncImage(
                model = selectedProfileImageUri,
                contentDescription = null,
                modifier = Modifier
                    .size(MoilGroupCreateDimension.ProfileAvatarImageSize)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

internal val profileAvatarResources = listOf(
    R.drawable.family_avatar_sibling,
    R.drawable.family_avatar_dad,
    R.drawable.family_avatar_member_blue,
    R.drawable.family_avatar_member_green,
    R.drawable.family_avatar_member_teal,
    R.drawable.family_avatar_mine,
    R.drawable.family_avatar_mom,
).reversed()
