package com.example.moil.feature.group.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.core.component.MoilPrimaryButton
import com.example.moil.ui.theme.MoilGroupCreateDimension
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
