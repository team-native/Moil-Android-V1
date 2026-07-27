package com.example.moil.feature.family.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.core.component.MoilNavigationDestination
import com.example.moil.core.component.MoilSwitch
import com.example.moil.core.component.MoilTabScaffold
import com.example.moil.core.model.GroupMemberRole
import com.example.moil.core.model.GroupProfileColor
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.LocalMoilExtraTypography
import com.example.moil.ui.theme.MoilMemberDimension
import com.example.moil.ui.theme.MoilTheme

@Composable
internal fun MemberScreenContent(
    uiState: FamilyUiState,
    onEvent: (FamilyScreenEvent) -> Unit,
) {
    MoilTabScaffold(
        selectedDestination = MoilNavigationDestination.Family,
        onDestinationClick = { destination ->
            onEvent(FamilyScreenEvent.DestinationClicked(destination))
        },
        contentHorizontalPadding = MoilMemberDimension.ScreenHorizontalPadding,
    ) { contentModifier ->
        Column(
            modifier = contentModifier.verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(R.string.family_title),
                style = LocalMoilExtraTypography.current.groupJoinTitle,
            )

            Spacer(modifier = Modifier.height(MoilMemberDimension.HeaderSubtitleTopSpacing))

            Text(
                text = memberGroupDisplayName(uiState.selectedGroup),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.height(MoilMemberDimension.GroupTabTopSpacing))

            FamilyGroupTabs(
                groups = uiState.groups,
                selectedGroupId = uiState.selectedGroupId,
                onGroupClick = { groupId ->
                    onEvent(FamilyScreenEvent.GroupClicked(groupId))
                },
            )

            Spacer(modifier = Modifier.height(MoilMemberDimension.SectionHeaderTopSpacing))

            FamilySectionLabel(text = stringResource(R.string.family_member_section))

            Spacer(modifier = Modifier.height(MoilMemberDimension.SectionLabelBottomSpacing))

            FamilyMemberCard(
                members = uiState.selectedGroup.members,
                memberRoleOverrides = uiState.memberRoleOverrides,
                groupProfileColor = uiState.selectedGroup.profileColor,
            )

            Spacer(modifier = Modifier.height(MoilMemberDimension.SectionSpacing))

            FamilyInviteCodeCard(
                inviteCode = uiState.selectedGroup.customInviteCode
                    ?: stringResource(requireNotNull(uiState.selectedGroup.inviteCodeRes)),
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
}

@Composable
private fun FamilyGroupTabs(
    groups: List<GroupUiModel>,
    selectedGroupId: String,
    onGroupClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MoilMemberDimension.GroupTabSpacing),
    ) {
        groups.forEach { group ->
            val isSelected = group.id == selectedGroupId

            Surface(
                modifier = Modifier
                    .height(MoilMemberDimension.GroupTabHeight)
                    .weight(1f)
                    .clickable { onGroupClick(group.id) },
                shape = RoundedCornerShape(percent = 50),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface
                },
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = memberGroupDisplayName(group),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun FamilySectionLabel(text: String) {
    Text(
        text = text,
        color = LocalMoilExtraColors.current.scheduleMutedText,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
private fun FamilyMemberCard(
    members: List<FamilyMemberUiModel>,
    memberRoleOverrides: Map<Int, Int>,
    groupProfileColor: GroupProfileColor,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilMemberDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Column {
            members.forEachIndexed { index, member ->
                FamilyMemberRow(
                    nameRes = member.nameRes,
                    avatarRes = member.avatarRes,
                    roleRes = memberRoleOverrides[member.nameRes] ?: member.roleRes,
                    customName = member.customName,
                    presenceColor = memberPresenceColor(
                        memberIndex = index,
                        groupProfileColor = groupProfileColor,
                    ),
                )

                if (index < members.lastIndex) {
                    HorizontalDivider(color = LocalMoilExtraColors.current.scheduleDivider)
                }
            }
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
private fun memberGroupDisplayName(group: GroupUiModel): String = group.customName
    ?: stringResource(requireNotNull(group.nameRes))

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

@Composable
private fun memberPresenceColor(
    memberIndex: Int,
    groupProfileColor: GroupProfileColor,
): Color = when (memberIndex) {
    0 -> profileColor(groupProfileColor)
    1 -> LocalMoilExtraColors.current.memberRose
    2 -> LocalMoilExtraColors.current.calendarEventGreen
    else -> LocalMoilExtraColors.current.calendarEventYellow
}

@Composable
private fun profileColor(profileColor: GroupProfileColor): Color = when (profileColor) {
    GroupProfileColor.Cyan -> LocalMoilExtraColors.current.calendarEventBlue
    GroupProfileColor.Violet -> LocalMoilExtraColors.current.memberViolet
    GroupProfileColor.Rose -> LocalMoilExtraColors.current.memberRose
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
