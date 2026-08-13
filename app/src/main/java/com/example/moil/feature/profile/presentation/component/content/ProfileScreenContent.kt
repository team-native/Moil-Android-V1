package com.example.moil.feature.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import com.example.moil.R
import com.example.moil.ui.theme.LocalMoilIsDarkTheme
import com.example.moil.core.component.MoilNavigationDestination
import com.example.moil.core.component.MoilSwitch
import com.example.moil.core.component.MoilTabScaffold
import com.example.moil.ui.theme.LocalMoilExtraColors
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

@Composable
private fun ProfileHeader() {
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

@Composable
private fun ProfileSectionLabel(text: String) {
    Text(
        text = text,
        color = LocalMoilExtraColors.current.scheduleMutedText,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
private fun ProfileGroupCard(
    groups: List<ProfileGroupUiModel>,
    onGroupClick: (String) -> Unit,
    onCreateGroupClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilProfileDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Column {
            groups.forEachIndexed { index, group ->
                ProfileGroupRow(
                    group = group,
                    showDivider = index < groups.lastIndex,
                    onClick = { onGroupClick(group.id) },
                )
            }

            if (groups.isNotEmpty()) {
                HorizontalDivider(color = LocalMoilExtraColors.current.scheduleDivider)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MoilProfileDimension.GroupRowHeight)
                    .clickable(onClick = onCreateGroupClick)
                    .padding(horizontal = MoilProfileDimension.GroupRowHorizontalPadding),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = stringResource(R.string.profile_create_group),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}

@Composable
private fun ProfileGroupRow(
    group: ProfileGroupUiModel,
    showDivider: Boolean,
    onClick: () -> Unit,
) {
    val groupChevronDrawableRes = if (LocalMoilIsDarkTheme.current) {
        R.drawable.common_chevron_next_dark
    } else {
        R.drawable.common_chevron_next
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilProfileDimension.GroupRowHeight)
                .clickable(onClick = onClick)
                .padding(horizontal = MoilProfileDimension.GroupRowHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(
                modifier = Modifier
                    .size(MoilProfileDimension.GroupIndicatorSize)
                    .clip(CircleShape)
                    .background(profileGroupIndicatorColor(group.indicator)),
            )

            Spacer(modifier = Modifier.width(MoilProfileDimension.GroupRowContentSpacing))

            Text(
                text = group.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall,
            )

            Image(
                painter = painterResource(groupChevronDrawableRes),
                contentDescription = null,
                modifier = Modifier.size(
                    width = MoilProfileDimension.ChevronWidth,
                    height = MoilProfileDimension.ChevronHeight,
                ),
                contentScale = ContentScale.Fit,
            )
        }

        if (showDivider) {
            HorizontalDivider(color = LocalMoilExtraColors.current.scheduleDivider)
        }
    }
}

@Composable
private fun profileGroupIndicatorColor(indicator: ProfileGroupIndicator) = when (indicator) {
    ProfileGroupIndicator.Primary -> LocalMoilExtraColors.current.profileGroupPrimaryIndicator
    ProfileGroupIndicator.Secondary -> LocalMoilExtraColors.current.profileGroupSecondaryIndicator
}

@Composable
private fun ProfileDarkThemeCard(
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilProfileDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilProfileDimension.SettingRowHeight)
                .padding(horizontal = MoilProfileDimension.GroupRowHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.profile_dark_mode),
                style = MaterialTheme.typography.bodyMedium,
            )

            MoilSwitch(
                checked = isDarkTheme,
                onCheckedChange = onDarkThemeChanged,
            )
        }
    }
}

@Composable
private fun ProfileLogoutButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilProfileDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilProfileDimension.LogoutHeight)
                .clickable(onClick = onClick)
                .padding(horizontal = MoilProfileDimension.GroupRowHorizontalPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.profile_logout),
                color = LocalMoilExtraColors.current.profileLogout,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}
