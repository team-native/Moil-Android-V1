package com.example.moil.feature.family.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.moil.R
import com.example.moil.core.model.GroupProfileColor
import com.example.moil.core.model.GroupMemberRole

data class FamilyUiState(
    val groups: List<GroupUiModel> = initialGroups(),
    val selectedGroupId: String = GROUP_ID_FAMILY,
    val memberRoleOverrides: Map<Int, Int> = emptyMap(),
    val notificationsEnabled: Boolean = true,
    val currentUserRole: GroupMemberRole = GroupMemberRole.Administrator,
) {
    val selectedGroup: GroupUiModel
        get() = groups.first { group -> group.id == selectedGroupId }

    val selectedGroupCurrentUserRole: GroupMemberRole
        get() {
            val currentUserMember = selectedGroup.members.firstOrNull { member ->
                member.nameRes == R.string.family_member_me
            }
            val currentUserRoleRes = currentUserMember?.let { member ->
                memberRoleOverrides[member.nameRes] ?: member.roleRes
            }

            return if (currentUserRoleRes == R.string.family_member_administrator) {
                GroupMemberRole.Administrator
            } else {
                GroupMemberRole.Member
            }
        }
}

data class GroupUiModel(
    val id: String,
    @param:StringRes val nameRes: Int? = null,
    val customName: String? = null,
    @param:StringRes val inviteCodeRes: Int? = null,
    val customInviteCode: String? = null,
    val profileColor: GroupProfileColor,
    val members: List<FamilyMemberUiModel>,
)

private const val GROUP_ID_FAMILY = "family"
private const val GROUP_ID_COLLEGE = "college"
private const val GROUP_ID_WORK = "work"

private fun initialGroups(): List<GroupUiModel> = listOf(
    GroupUiModel(
        id = GROUP_ID_FAMILY,
        nameRes = R.string.family_subtitle,
        inviteCodeRes = R.string.family_group_invite_code,
        profileColor = GroupProfileColor.Cyan,
        members = listOf(
            FamilyMemberUiModel(R.string.family_member_dad, R.drawable.family_avatar_dad, R.string.family_member_administrator),
            FamilyMemberUiModel(R.string.family_member_mom, R.drawable.family_avatar_mom, R.string.family_member_role),
            FamilyMemberUiModel(R.string.family_member_me, R.drawable.family_avatar_mine, R.string.family_member_role),
            FamilyMemberUiModel(R.string.family_member_sister, R.drawable.family_avatar_sibling, R.string.family_member_role),
        ),
    ),
    GroupUiModel(
        id = GROUP_ID_COLLEGE,
        nameRes = R.string.family_group_college,
        inviteCodeRes = R.string.family_group_college_invite_code,
        profileColor = GroupProfileColor.Violet,
        members = listOf(
            FamilyMemberUiModel(R.string.family_member_me, R.drawable.family_avatar_mine, R.string.family_member_administrator),
            FamilyMemberUiModel(R.string.family_member_jimin, R.drawable.family_avatar_member_green, R.string.family_member_role),
            FamilyMemberUiModel(R.string.family_member_seoyeon, R.drawable.family_avatar_member_teal, R.string.family_member_role),
        ),
    ),
    GroupUiModel(
        id = GROUP_ID_WORK,
        nameRes = R.string.family_group_work,
        inviteCodeRes = R.string.family_group_work_invite_code,
        profileColor = GroupProfileColor.Rose,
        members = listOf(
            FamilyMemberUiModel(R.string.family_member_me, R.drawable.family_avatar_mine, R.string.family_member_role),
            FamilyMemberUiModel(R.string.family_member_jimin, R.drawable.family_avatar_member_green, R.string.family_member_administrator),
        ),
    ),
)

data class FamilyMemberUiModel(
    @param:StringRes val nameRes: Int,
    @param:DrawableRes val avatarRes: Int,
    @param:StringRes val roleRes: Int,
    val customName: String? = null,
)
