package com.example.moil.feature.family.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.core.component.MoilNavigationDestination
import com.example.moil.core.component.MoilSwitch
import com.example.moil.core.component.MoilTabScaffold
import com.example.moil.core.component.content.MoilEmptyJoinedGroupContent
import com.example.moil.core.model.GroupMemberRole
import com.example.moil.feature.family.presentation.FamilyMemberHeader
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilMemberDimension
import com.example.moil.ui.theme.MoilSpacing
import com.example.moil.ui.theme.MoilTheme

@Composable
internal fun MemberScreenContent(
    uiState: FamilyUiState,
    onEvent: (FamilyScreenEvent) -> Unit,
) {
    val isEmptyGroupContentVisible = uiState.selectedGroup == null

    MoilTabScaffold(
        selectedDestination = MoilNavigationDestination.Family,
        onDestinationClick = { destination ->
            onEvent(FamilyScreenEvent.DestinationClicked(destination))
        },
        contentHorizontalPadding = if (isEmptyGroupContentVisible) {
            MoilSpacing.ScreenHorizontal
        } else {
            MoilMemberDimension.ScreenHorizontalPadding
        },
        contentVerticalPadding = if (isEmptyGroupContentVisible) 0.dp else MoilSpacing.HeaderTop,
    ) { contentModifier ->
        val selectedGroup = uiState.selectedGroup

        when {
            uiState.isGroupsLoading -> {
                FamilyGroupLoadingContent(modifier = contentModifier)
            }
            uiState.hasGroupLoadError -> {
                FamilyGroupErrorContent(modifier = contentModifier)
            }
            selectedGroup == null -> {
                MoilEmptyJoinedGroupContent(
                    onJoinGroupClick = { onEvent(FamilyScreenEvent.EmptyGroupJoinClicked) },
                    onCreateGroupClick = { onEvent(FamilyScreenEvent.EmptyGroupCreateClicked) },
                    modifier = contentModifier,
                )
            }
            else -> {
                MemberGroupContent(
                    selectedGroup = selectedGroup,
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = contentModifier,
                )
            }
        }
    }
}

@Composable
private fun MemberGroupContent(
    selectedGroup: GroupUiModel,
    uiState: FamilyUiState,
    onEvent: (FamilyScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        FamilyMemberHeader()

        Spacer(modifier = Modifier.height(MoilMemberDimension.GroupTabTopSpacing))

        FamilyGroupTabs(
            groups = uiState.groups,
            selectedGroupId = selectedGroup.id,
            onGroupClick = { groupId ->
                onEvent(FamilyScreenEvent.GroupClicked(groupId))
            },
        )

        Spacer(modifier = Modifier.height(MoilMemberDimension.SectionHeaderTopSpacing))

        FamilySectionLabel(text = stringResource(R.string.family_member_section))

        Spacer(modifier = Modifier.height(MoilMemberDimension.SectionLabelBottomSpacing))

        FamilyMemberCard(
            members = selectedGroup.members,
            memberRoleOverrides = uiState.memberRoleOverrides,
        )

        Spacer(modifier = Modifier.height(MoilMemberDimension.SectionSpacing))

        FamilyInviteCodeCard(
            inviteCode = selectedGroup.inviteCode,
        )

        Spacer(modifier = Modifier.height(MoilMemberDimension.SectionSpacing))

        FamilySectionLabel(
            text = if (uiState.selectedGroupCurrentUserRole == GroupMemberRole.Administrator) {
                stringResource(R.string.family_group_settings_administrator)
            } else {
                stringResource(R.string.family_group_settings)
            },
        )

        Spacer(modifier = Modifier.height(MoilMemberDimension.SectionLabelBottomSpacing))

        if (uiState.selectedGroupCurrentUserRole == GroupMemberRole.Administrator) {
            FamilyAdministratorSettingsContent(
                notificationsEnabled = uiState.notificationsEnabled,
                onNotificationsChanged = { isEnabled ->
                    onEvent(FamilyScreenEvent.NotificationsChanged(isEnabled))
                },
                onGroupNameChangeClick = {
                    onEvent(FamilyScreenEvent.GroupNameChangeClicked)
                },
                onMemberPermissionsClick = {
                    onEvent(FamilyScreenEvent.MemberPermissionsClicked)
                },
                onInviteLinkShareClick = {
                    onEvent(FamilyScreenEvent.InviteLinkShareClicked)
                },
                onLeaveClick = {
                    onEvent(FamilyScreenEvent.BackClicked)
                },
            )
        } else {
            FamilyMemberSettingsContent(
                notificationsEnabled = uiState.notificationsEnabled,
                onNotificationsChanged = { isEnabled ->
                    onEvent(FamilyScreenEvent.NotificationsChanged(isEnabled))
                },
                onLeaveClick = {
                    onEvent(FamilyScreenEvent.BackClicked)
                },
            )
        }
    }
}

@Composable
private fun FamilyInviteCodeCard(inviteCode: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilMemberDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MoilMemberDimension.ListItemHorizontalPadding,
                    vertical = MoilMemberDimension.InviteCodeVerticalPadding,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.family_group_invite_code_label),
                    style = MaterialTheme.typography.labelMedium,
                )

                Spacer(modifier = Modifier.height(MoilMemberDimension.InviteCodeLabelSpacing))

                Text(
                    text = inviteCode,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Surface(
                modifier = Modifier.size(
                    width = MoilMemberDimension.InviteCodeActionWidth,
                    height = MoilMemberDimension.InviteCodeActionHeight,
                ),
                shape = RoundedCornerShape(percent = 50),
                color = MaterialTheme.colorScheme.primary,
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.family_group_invite_copy),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

@Composable
internal fun FamilyMemberSettingsContent(
    notificationsEnabled: Boolean,
    onNotificationsChanged: (Boolean) -> Unit,
    onLeaveClick: () -> Unit,
) {
    FamilySettingsCard {
        FamilyNotificationSettingRow(
            notificationsEnabled = notificationsEnabled,
            onNotificationsChanged = onNotificationsChanged,
        )

        FamilySettingDivider()

        FamilyLeaveSettingRow(onClick = onLeaveClick)
    }
}

@Composable
internal fun FamilyAdministratorSettingsContent(
    notificationsEnabled: Boolean,
    onNotificationsChanged: (Boolean) -> Unit,
    onGroupNameChangeClick: () -> Unit,
    onMemberPermissionsClick: () -> Unit,
    onInviteLinkShareClick: () -> Unit,
    onLeaveClick: () -> Unit,
) {
    FamilySettingsCard {
        FamilyNotificationSettingRow(
            notificationsEnabled = notificationsEnabled,
            onNotificationsChanged = onNotificationsChanged,
        )

        FamilySettingDivider()

        FamilyActionSettingRow(
            label = stringResource(R.string.family_group_name_change),
            onClick = onGroupNameChangeClick,
        )

        FamilySettingDivider()

        FamilyActionSettingRow(
            label = stringResource(R.string.family_group_member_permissions),
            onClick = onMemberPermissionsClick,
        )

        FamilySettingDivider()

        FamilyActionSettingRow(
            label = stringResource(R.string.family_group_social_invite_share),
            onClick = onInviteLinkShareClick,
        )

        FamilySettingDivider()

        FamilyLeaveSettingRow(onClick = onLeaveClick)
    }
}

@Composable
private fun FamilySettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilMemberDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Column(content = content)
    }
}

@Composable
private fun memberGroupDisplayName(group: GroupUiModel): String = group.name

@Composable
private fun FamilyNotificationSettingRow(
    notificationsEnabled: Boolean,
    onNotificationsChanged: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(MoilMemberDimension.SettingsRowHeight)
            .padding(horizontal = MoilMemberDimension.ListItemHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.family_group_notifications),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
        )

        MoilSwitch(
            checked = notificationsEnabled,
            onCheckedChange = onNotificationsChanged,
        )
    }
}

@Composable
private fun FamilyActionSettingRow(
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(MoilMemberDimension.SettingsRowHeight)
            .clickable(onClick = onClick)
            .padding(horizontal = MoilMemberDimension.ListItemHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
        )

        Text(
            text = stringResource(R.string.family_setting_next),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun FamilyLeaveSettingRow(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(MoilMemberDimension.SettingsRowHeight)
            .clickable(onClick = onClick)
            .padding(horizontal = MoilMemberDimension.ListItemHorizontalPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = stringResource(R.string.family_group_leave),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun FamilySettingDivider() {
    HorizontalDivider(
        color = LocalMoilExtraColors.current.scheduleDivider,
    )
}

@Preview(showBackground = true)
@Composable
private fun FamilyMemberSettingsContentPreview() {
    MoilTheme(darkTheme = false) {
        FamilyMemberSettingsContent(
            notificationsEnabled = true,
            onNotificationsChanged = {},
            onLeaveClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FamilyAdministratorSettingsContentPreview() {
    MoilTheme(darkTheme = false) {
        FamilyAdministratorSettingsContent(
            notificationsEnabled = true,
            onNotificationsChanged = {},
            onGroupNameChangeClick = {},
            onMemberPermissionsClick = {},
            onInviteLinkShareClick = {},
            onLeaveClick = {},
        )
    }
}
