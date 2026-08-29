package com.example.moil.feature.group.viewmodel

import com.example.moil.core.domain.MoilError
import com.example.moil.feature.group.module.domain.model.GroupDetail
import com.example.moil.feature.group.module.domain.model.GroupMember
import com.example.moil.feature.group.module.domain.model.GroupSummary
import com.example.moil.feature.group.module.domain.model.InviteVerification

data class GroupUiState(
    val isLoading: Boolean = false,
    val groups: List<GroupSummary> = emptyList(),
    val selectedGroupId: Long? = null,
    val selectedGroupDetail: GroupDetail? = null,
    val members: List<GroupMember> = emptyList(),
    val inviteVerification: InviteVerification? = null,
    val joinGroupMembers: List<GroupMember> = emptyList(),
    val isCurrentUserNameMissing: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: MoilError? = null,
) {
    val selectedGroup: GroupSummary?
        get() = groups.firstOrNull { it.id == selectedGroupId }
}
