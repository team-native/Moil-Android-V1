package com.example.moil.feature.family.presentation

import com.example.moil.R
import com.example.moil.core.model.GroupMemberRole
import com.example.moil.core.model.GroupProfileColor
import com.example.moil.feature.group.domain.GroupColor
import com.example.moil.feature.group.domain.GroupMember
import com.example.moil.feature.group.domain.GroupRole
import com.example.moil.feature.group.domain.GroupSummary

internal fun List<GroupSummary>.toFamilyGroups(
    selectedGroupId: Long?,
    selectedGroupMembers: List<GroupMember>,
): List<GroupUiModel> = map { group ->
    group.toFamilyGroup(
        members = if (group.id == selectedGroupId) selectedGroupMembers else emptyList(),
    )
}

internal fun GroupRole.toFamilyMemberRole(): GroupMemberRole = when (this) {
    GroupRole.Owner,
    GroupRole.Admin,
    -> GroupMemberRole.Administrator
    GroupRole.Member,
    GroupRole.Unknown,
    -> GroupMemberRole.Member
}

private fun GroupSummary.toFamilyGroup(members: List<GroupMember>): GroupUiModel = GroupUiModel(
    id = id.toString(),
    name = name,
    inviteCode = inviteCode.orEmpty(),
    profileColor = myColor.toFamilyProfileColor(),
    members = members.map(GroupMember::toFamilyMember),
)

private fun GroupMember.toFamilyMember(): FamilyMemberUiModel = FamilyMemberUiModel(
    id = userId,
    roleRes = role.toFamilyRoleRes(),
    name = nickname,
    profileColor = color.toFamilyProfileColor(),
    isCurrentUser = isMe == true,
)

private fun GroupColor.toFamilyProfileColor(): GroupProfileColor = when (this) {
    GroupColor.Violet,
    GroupColor.Magenta,
    -> GroupProfileColor.Violet
    GroupColor.Red -> GroupProfileColor.Rose
    GroupColor.Sky,
    GroupColor.Green,
    GroupColor.Yellow,
    GroupColor.Teal,
    GroupColor.Unknown,
    -> GroupProfileColor.Cyan
}

private fun GroupRole.toFamilyRoleRes(): Int = when (this) {
    GroupRole.Owner,
    GroupRole.Admin,
    -> R.string.family_member_administrator
    GroupRole.Member,
    GroupRole.Unknown,
    -> R.string.family_member_role
}
