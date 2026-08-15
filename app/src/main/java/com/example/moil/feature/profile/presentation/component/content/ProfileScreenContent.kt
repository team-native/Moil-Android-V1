package com.example.moil.feature.profile.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.MoilNavigationDestination
import com.example.moil.core.component.MoilTabScaffold
import com.example.moil.ui.theme.MoilProfileDimension

@Composable
internal fun ProfileScreenContent(
    uiState: ProfileUiState,
    groups: List<ProfileGroupUiModel>,
    onEvent: (ProfileScreenEvent) -> Unit,
) {
    MoilTabScaffold(
        selectedDestination = MoilNavigationDestination.Profile,
        onDestinationClick = { destination ->
            onEvent(ProfileScreenEvent.DestinationClicked(destination))
        },
        contentHorizontalPadding = MoilProfileDimension.ScreenHorizontalPadding,
    ) { contentModifier ->
        Column(
            modifier = contentModifier.verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(MoilProfileDimension.HeaderTopPadding))

            ProfileAccountHeader(
                profileName = uiState.profileName,
                profileAvatarRes = uiState.profileAvatarRes,
                onProfileImageClick = {
                    onEvent(ProfileScreenEvent.ProfileImageClicked)
                },
            )

            Spacer(modifier = Modifier.height(MoilProfileDimension.SectionLabelTopPadding))

            ProfileSectionLabel(text = stringResource(R.string.profile_group_list_label))

            Spacer(modifier = Modifier.height(MoilProfileDimension.SectionLabelBottomPadding))

            ProfileGroupCard(
                groups = groups,
                onGroupClick = { groupId ->
                    onEvent(ProfileScreenEvent.GroupClicked(groupId))
                },
                onCreateGroupClick = {
                    onEvent(ProfileScreenEvent.CreateGroupClicked)
                },
            )

            Spacer(modifier = Modifier.height(MoilProfileDimension.SettingsLabelTopPadding))

            ProfileSectionLabel(text = stringResource(R.string.profile_settings_label))

            Spacer(modifier = Modifier.height(MoilProfileDimension.SettingsLabelBottomPadding))

            ProfileDarkThemeCard(
                isDarkTheme = uiState.isDarkTheme,
                onDarkThemeChanged = { isDarkTheme ->
                    onEvent(ProfileScreenEvent.DarkThemeChanged(isDarkTheme))
                },
            )

            Spacer(modifier = Modifier.height(MoilProfileDimension.LogoutTopPadding))

            ProfileLogoutButton(
                onClick = { onEvent(ProfileScreenEvent.LogoutClicked) },
            )
        }
    }
}
