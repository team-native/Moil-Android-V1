package com.example.moil.feature.group.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.core.component.MoilPrimaryButton
import com.example.moil.core.component.MoilTextField
import com.example.moil.ui.theme.MoilGroupCreateDimension
import com.example.moil.ui.theme.MoilMemberDimension
import com.example.moil.ui.theme.MoilTheme
import com.example.moil.ui.theme.LocalMoilExtraTypography
import com.example.moil.feature.group.presentation.avatarResourceForGroupColor
import com.example.moil.feature.group.presentation.groupColorForAvatar
import com.example.moil.feature.group.domain.GroupColor

@Composable
internal fun JoinGroupProfileSetupContent(
    uiState: JoinGroupUiState,
    onEvent: (JoinGroupScreenEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = MoilGroupCreateDimension.ScreenHorizontalPadding),
    ) {
        GroupPageHeader(
            titleRes = R.string.group_join_profile_setup_title,
            onBackClick = { onEvent(JoinGroupScreenEvent.ProfileSetupBackClicked) },
            titleStyle = LocalMoilExtraTypography.current.groupJoinTitle,
        )

        Spacer(modifier = Modifier.height(MoilGroupCreateDimension.LabelTopPadding))

        JoinedGroupSummary(
            groupName = uiState.verifiedGroupName,
            memberCount = uiState.verifiedMemberCount,
        )

        Spacer(modifier = Modifier.height(MoilGroupCreateDimension.LabelTopPadding))

        ProfileNameField(
            profileName = uiState.profileName,
            onProfileNameChanged = { profileName ->
                onEvent(JoinGroupScreenEvent.ProfileNameChanged(profileName))
            },
        )

        Spacer(modifier = Modifier.height(MoilGroupCreateDimension.LabelTopPadding))

        UsedProfileList(profiles = uiState.usedProfiles)

        Spacer(modifier = Modifier.height(MoilGroupCreateDimension.LabelTopPadding))

        ProfileAvatarSelector(
            labelRes = R.string.group_join_profile_color_label,
            selectedProfileAvatarRes = uiState.selectedProfileColor
                ?.let(::avatarResourceForGroupColor)
                ?: R.drawable.family_avatar_mine,
            onProfileAvatarSelected = { avatarRes ->
                onEvent(JoinGroupScreenEvent.ProfileColorSelected(groupColorForAvatar(avatarRes)))
            },
            avatarResources = uiState.availableProfileColors.map(::avatarResourceForGroupColor),
        )

        Spacer(modifier = Modifier.weight(1f))

        MoilPrimaryButton(
            text = stringResource(R.string.group_join_complete_action),
            onClick = { onEvent(JoinGroupScreenEvent.JoinGroupConfirmed) },
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilGroupCreateDimension.BottomButtonHeight),
            enabled = uiState.profileName.isNotBlank() && uiState.selectedProfileColor != null,
        )

        Spacer(modifier = Modifier.height(MoilGroupCreateDimension.BottomButtonPadding))
    }
}

@Composable
private fun JoinedGroupSummary(
    groupName: String,
    memberCount: Int,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        Row(
            modifier = Modifier.padding(MoilMemberDimension.ListItemHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = groupName,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Text(
                    text = stringResource(R.string.group_member_count_format, memberCount),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun ProfileNameField(
    profileName: String,
    onProfileNameChanged: (String) -> Unit,
) {
    Column {
        Text(
            text = stringResource(R.string.group_join_profile_name_label),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )

        MoilTextField(
            value = profileName,
            onValueChange = onProfileNameChanged,
            placeholder = stringResource(R.string.group_join_profile_name_placeholder),
            modifier = Modifier.padding(top = MoilGroupCreateDimension.HeaderTitleSpacing),
        )
    }
}

@Composable
private fun UsedProfileList(profiles: List<JoinGroupUsedProfileUiModel>) {
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
                    Image(
                        painter = painterResource(avatarResourceForGroupColor(profile.color)),
                        contentDescription = profile.nickname,
                        modifier = Modifier
                            .size(MoilGroupCreateDimension.ProfileAvatarImageSize)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
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

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun JoinGroupProfileSetupContentPreview() {
    MoilTheme(darkTheme = false) {
        JoinGroupProfileSetupContent(
            uiState = JoinGroupUiState(
                step = JoinGroupStep.ProfileSetup,
                usedProfiles = listOf(
                    JoinGroupUsedProfileUiModel(
                        nickname = "모일",
                        color = GroupColor.Red,
                    ),
                ),
                availableProfileColors = listOf(
                    GroupColor.Sky,
                    GroupColor.Green,
                    GroupColor.Yellow,
                ),
                selectedProfileColor = GroupColor.Sky,
            ),
            onEvent = {},
        )
    }
}
