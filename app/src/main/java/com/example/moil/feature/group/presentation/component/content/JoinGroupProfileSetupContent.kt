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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.core.component.MoilGroupAvatarStack
import com.example.moil.core.component.MoilPrimaryButton
import com.example.moil.core.component.MoilTextField
import com.example.moil.ui.theme.MoilGroupCreateDimension
import com.example.moil.ui.theme.MoilMemberDimension
import com.example.moil.ui.theme.MoilSpacing
import com.example.moil.ui.theme.MoilTheme
import com.example.moil.ui.theme.LocalMoilExtraTypography

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

        JoinedGroupSummary()

        Spacer(modifier = Modifier.height(MoilGroupCreateDimension.LabelTopPadding))

        ProfileNameField(
            profileName = uiState.profileName,
            onProfileNameChanged = { profileName ->
                onEvent(JoinGroupScreenEvent.ProfileNameChanged(profileName))
            },
        )

        Spacer(modifier = Modifier.height(MoilGroupCreateDimension.LabelTopPadding))

        UsedProfileList()

        Spacer(modifier = Modifier.height(MoilGroupCreateDimension.LabelTopPadding))

        ProfileAvatarSelector(
            labelRes = R.string.group_join_profile_color_label,
            selectedProfileAvatarRes = uiState.selectedProfileAvatarRes,
            onProfileAvatarSelected = { avatarRes ->
                onEvent(JoinGroupScreenEvent.ProfileAvatarSelected(avatarRes))
            },
            avatarResources = profileColorAvatarResources,
        )

        Spacer(modifier = Modifier.weight(1f))

        MoilPrimaryButton(
            text = stringResource(R.string.group_join_complete_action),
            onClick = { onEvent(JoinGroupScreenEvent.JoinGroupConfirmed) },
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilGroupCreateDimension.BottomButtonHeight),
            enabled = uiState.profileName.isNotBlank(),
        )

        Spacer(modifier = Modifier.height(MoilGroupCreateDimension.BottomButtonPadding))
    }
}

@Composable
private fun JoinedGroupSummary() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        Row(
            modifier = Modifier.padding(MoilMemberDimension.ListItemHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MoilGroupAvatarStack(groupMemberAvatarResources = joinedGroupMemberAvatarResources)

            Spacer(modifier = Modifier.width(MoilSpacing.CalendarRow))

            Column {
                Text(
                    text = stringResource(R.string.calendar_family_name),
                    style = MaterialTheme.typography.bodyMedium,
                )

                Text(
                    text = stringResource(R.string.group_join_member_count),
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
private fun UsedProfileList() {
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
            usedProfiles.forEach { profile ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(profile.avatarRes),
                        contentDescription = stringResource(profile.nameRes),
                        modifier = Modifier
                            .size(MoilGroupCreateDimension.ProfileAvatarImageSize)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )

                    Text(
                        text = stringResource(profile.nameRes),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

private data class UsedProfile(
    val avatarRes: Int,
    val nameRes: Int,
)

private val joinedGroupMemberAvatarResources = listOf(
    R.drawable.family_avatar_dad,
    R.drawable.family_avatar_mom,
    R.drawable.family_avatar_member_green,
    R.drawable.family_avatar_sibling,
)

private val usedProfiles = listOf(
    UsedProfile(R.drawable.family_avatar_dad, R.string.family_member_dad),
    UsedProfile(R.drawable.family_avatar_mom, R.string.family_member_mom),
    UsedProfile(R.drawable.family_avatar_member_green, R.string.family_member_me),
    UsedProfile(R.drawable.family_avatar_sibling, R.string.family_member_sister),
)

private val profileColorAvatarResources = listOf(
    R.drawable.family_avatar_mine,
    R.drawable.family_avatar_member_teal,
    R.drawable.family_avatar_mom,
)

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun JoinGroupProfileSetupContentPreview() {
    MoilTheme(darkTheme = false) {
        JoinGroupProfileSetupContent(
            uiState = JoinGroupUiState(
                step = JoinGroupStep.ProfileSetup,
            ),
            onEvent = {},
        )
    }
}
