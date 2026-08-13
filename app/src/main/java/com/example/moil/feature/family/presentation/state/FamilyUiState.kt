package com.example.moil.feature.family.presentation

import androidx.annotation.StringRes
import com.example.moil.core.model.GroupMemberRole
import com.example.moil.feature.group.domain.GroupColor
import java.time.YearMonth

data class FamilyUiState(
    val groups: List<GroupUiModel> = emptyList(),
    val selectedGroupId: String? = null,
    val memberRoleOverrides: Map<Long, Int> = emptyMap(),
    val notificationsEnabled: Boolean = true,
    val isGroupsLoading: Boolean = false,
    val hasGroupLoadError: Boolean = false,
    val currentUserRole: GroupMemberRole = GroupMemberRole.Member,
    val currentMonth: YearMonth = YearMonth.now(),
    val currentMonthEventCount: Int = 0,
) {
    val selectedGroup: GroupUiModel?
        get() = groups.firstOrNull { group -> group.id == selectedGroupId }

    val selectedGroupCurrentUserRole: GroupMemberRole
        get() = currentUserRole
}

data class GroupUiModel(
    val id: String,
    val name: String,
    val inviteCode: String,
    val profileColor: GroupColor,
    val members: List<FamilyMemberUiModel>,
)

data class FamilyMemberUiModel(
    val id: Long,
    @param:StringRes val roleRes: Int,
    val name: String,
    val profileColor: GroupColor,
    val isCurrentUser: Boolean,
)
