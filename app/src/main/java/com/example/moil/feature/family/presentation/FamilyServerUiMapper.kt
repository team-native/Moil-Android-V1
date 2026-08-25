package com.example.moil.feature.family.presentation

import com.example.moil.R
import com.example.moil.core.model.GroupMemberRole
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.GroupMember
import com.example.moil.feature.group.module.domain.model.GroupRole
import com.example.moil.feature.group.module.domain.model.GroupSummary

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
    profileColor = myColor,
    members = members.map(GroupMember::toFamilyMember),
)

private fun GroupMember.toFamilyMember(): FamilyMemberUiModel = FamilyMemberUiModel(
    id = userId,
    roleRes = role.toFamilyRoleRes(),
    name = nickname,
    profileColor = color,
    isCurrentUser = isMe == true,
)

private fun GroupRole.toFamilyRoleRes(): Int = when (this) {
    GroupRole.Owner,
    GroupRole.Admin,
    -> R.string.family_member_administrator
    GroupRole.Member,
    GroupRole.Unknown,
    -> R.string.family_member_role
}
