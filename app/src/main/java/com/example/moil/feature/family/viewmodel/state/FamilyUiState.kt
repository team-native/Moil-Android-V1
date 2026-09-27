package com.example.moil.feature.family.viewmodel

import androidx.annotation.StringRes
import com.example.moil.core.model.GroupMemberRole
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.viewmodel.GroupLeavePolicy
import java.time.YearMonth

data class FamilyUiState(
    val groups: List<GroupUiModel> = emptyList(),
    val selectedGroupId: String? = null,
    // 서버가 알림 설정값을 내려주지 않으면 null이며, 이때 토글은 임의 값을 보여주지 않고 비활성으로 둔다.
    val notificationsEnabled: Boolean? = null,
    val isNotificationUpdating: Boolean = false,
    val isManagementInProgress: Boolean = false,
    val leavePolicy: GroupLeavePolicy = GroupLeavePolicy.Confirm,
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

    val isNotificationToggleEnabled: Boolean
        get() = notificationsEnabled != null && !isNotificationUpdating
}

data class GroupUiModel(
    val id: String,
    val name: String,
    val inviteCode: String,
    val profileColor: GroupColor,
    val profileImagePath: String? = null,
    val members: List<FamilyMemberUiModel>,
)

data class FamilyMemberUiModel(
    val id: Long,
    @param:StringRes val roleRes: Int,
    val name: String,
    val profileColor: GroupColor,
    val profileImagePath: String? = null,
    val isCurrentUser: Boolean,
    val role: GroupMemberRole = GroupMemberRole.Member,
    // 권한 변경 API가 다룰 수 없는 역할(서버 전용 Owner, 알 수 없는 역할)이면 false다.
    val isRoleEditable: Boolean = true,
)
