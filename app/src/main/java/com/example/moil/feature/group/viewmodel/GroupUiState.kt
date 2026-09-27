package com.example.moil.feature.group.viewmodel

import com.example.moil.core.domain.MoilError
import com.example.moil.feature.group.module.domain.model.GroupDetail
import com.example.moil.feature.group.module.domain.model.GroupMember
import com.example.moil.feature.group.module.domain.model.GroupRole
import com.example.moil.feature.group.module.domain.model.GroupSummary
import com.example.moil.feature.group.module.domain.model.InviteVerification

/**
 * 그룹 목록·선택 그룹·가입 흐름의 서버 상태다.
 *
 * [loadError]는 그룹 목록·상세·멤버를 불러오지 못한 경우에만 채워진다.
 * 이름 변경·나가기 같은 개별 작업 실패는 화면 전체 오류가 되지 않도록 [GroupEffect.OperationFailed]로 한 번만 알린다.
 */
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
    val isManagementInProgress: Boolean = false,
    val isNotificationUpdating: Boolean = false,
    val loadError: MoilError? = null,
) {
    val selectedGroup: GroupSummary?
        get() = groups.firstOrNull { it.id == selectedGroupId }

    /** 선택 그룹 상세가 현재 선택과 일치할 때만 서버 알림 설정값을 제공한다. */
    val selectedGroupNotificationEnabled: Boolean?
        get() = selectedGroupDetail
            ?.takeIf { detail -> detail.id == selectedGroupId }
            ?.notificationEnabled

    /**
     * 선택 그룹을 나갈 때 거쳐야 하는 절차다.
     *
     * 서버는 유일한 관리자의 퇴장을 거부하고, 마지막 멤버가 나가면 그룹을 삭제한다(API 명세 "그룹 퇴장").
     * 멤버 목록을 아직 받지 못했으면 일반 확인으로 두고 최종 판단은 서버 응답에 맡긴다.
     */
    val leavePolicy: GroupLeavePolicy
        get() {
            val myRole = selectedGroup?.myRole ?: return GroupLeavePolicy.Confirm

            if (members.isEmpty()) {
                return GroupLeavePolicy.Confirm
            }

            if (members.size == 1) {
                return GroupLeavePolicy.LastMember
            }

            val isAdministrator = myRole.isAdministrator()
            val otherAdministratorCount = members.count { member ->
                member.isMe != true && member.role.isAdministrator()
            }

            return if (isAdministrator && otherAdministratorCount == 0) {
                GroupLeavePolicy.TransferRequired
            } else {
                GroupLeavePolicy.Confirm
            }
        }
}

/** 그룹 나가기 전에 사용자에게 보여줄 절차 종류다. */
enum class GroupLeavePolicy {
    /** 일반 나가기 확인만 받는다. */
    Confirm,

    /** 유일한 관리자라 다른 멤버에게 관리자 권한을 넘긴 뒤 나가야 한다. */
    TransferRequired,

    /** 혼자 남은 그룹이라 나가면 그룹이 삭제된다. */
    LastMember,
}

internal fun GroupRole.isAdministrator(): Boolean = this == GroupRole.Owner || this == GroupRole.Admin
