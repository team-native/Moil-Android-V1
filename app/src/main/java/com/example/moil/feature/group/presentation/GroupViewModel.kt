package com.example.moil.feature.group.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moil.core.domain.MoilError
import com.example.moil.core.domain.MoilResult
import com.example.moil.core.network.SessionManager
import com.example.moil.feature.group.domain.GetGroupMembersUseCase
import com.example.moil.feature.group.domain.GetGroupUseCase
import com.example.moil.feature.group.domain.GetMyGroupsUseCase
import com.example.moil.feature.group.domain.CreateGroupUseCase
import com.example.moil.feature.group.domain.JoinGroupUseCase
import com.example.moil.feature.group.domain.LeaveGroupUseCase
import com.example.moil.feature.group.domain.RenameGroupUseCase
import com.example.moil.feature.group.domain.TransferAdminUseCase
import com.example.moil.feature.group.domain.UpdateMemberRolesUseCase
import com.example.moil.feature.group.domain.VerifyInviteUseCase
import com.example.moil.feature.group.domain.GroupColor
import com.example.moil.feature.group.domain.GroupDetail
import com.example.moil.feature.group.domain.GroupMember
import com.example.moil.feature.group.domain.GroupRole
import com.example.moil.feature.group.domain.GroupSummary
import com.example.moil.feature.group.domain.InviteVerification
import com.example.moil.feature.group.domain.UpdateGroupNotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GroupUiState(
    val isLoading: Boolean = false,
    val groups: List<GroupSummary> = emptyList(),
    val selectedGroupId: Long? = null,
    val selectedGroupDetail: GroupDetail? = null,
    val members: List<GroupMember> = emptyList(),
    val inviteVerification: InviteVerification? = null,
    val joinGroupMembers: List<GroupMember> = emptyList(),
    val isCurrentUserNameMissing: Boolean = false,
    val error: MoilError? = null,
) {
    val selectedGroup: GroupSummary?
        get() = groups.firstOrNull { it.id == selectedGroupId }
}

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val getMyGroupsUseCase: GetMyGroupsUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val verifyInviteUseCase: VerifyInviteUseCase,
    private val joinGroupUseCase: JoinGroupUseCase,
    private val getGroupUseCase: GetGroupUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val updateGroupNotificationUseCase: UpdateGroupNotificationUseCase,
    private val renameGroupUseCase: RenameGroupUseCase,
    private val updateMemberRolesUseCase: UpdateMemberRolesUseCase,
    private val transferAdminUseCase: TransferAdminUseCase,
    private val leaveGroupUseCase: LeaveGroupUseCase,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(GroupUiState())
    val uiState: StateFlow<GroupUiState> = mutableUiState.asStateFlow()

    init {
        loadGroups()
    }

    /** 화면 진입과 재시도 이벤트에서 내 그룹 UseCase를 호출해 빈 상태·오류 상태를 분리합니다. */
    fun loadGroups(preferredGroupId: Long? = null) = viewModelScope.launch {
        mutableUiState.value = mutableUiState.value.copy(
            isLoading = true,
            isCurrentUserNameMissing = false,
            error = null,
        )
        when (val result = getMyGroupsUseCase()) {
            is MoilResult.Success -> {
                val selectedGroupId = preferredGroupId
                    ?.takeIf { candidateId -> result.value.any { group -> group.id == candidateId } }
                    ?: mutableUiState.value.selectedGroupId
                        ?.takeIf { candidateId -> result.value.any { group -> group.id == candidateId } }
                    ?: result.value.firstOrNull()?.id

                mutableUiState.value = mutableUiState.value.copy(
                    isLoading = false,
                    groups = result.value,
                    selectedGroupId = selectedGroupId,
                    members = if (selectedGroupId == null) emptyList() else mutableUiState.value.members,
                )

                if (selectedGroupId != null) {
                    loadGroupDetail(selectedGroupId)
                    loadMembers(selectedGroupId)
                }
            }
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(isLoading = false, error = result.error)
        }
    }

    /** 그룹 선택 이벤트에서 선택 그룹의 멤버 목록을 로드합니다. */
    fun selectGroup(groupId: Long) {
        mutableUiState.value = mutableUiState.value.copy(selectedGroupId = groupId, members = emptyList(), error = null)
        loadGroupDetail(groupId)
        loadMembers(groupId)
    }

    /** 그룹 설정의 알림 스위치 이벤트에서 서버 값을 갱신하며 실패 시 오류 상태만 갱신합니다. */
    fun updateNotification(enabled: Boolean) = viewModelScope.launch {
        val groupId = mutableUiState.value.selectedGroupId ?: return@launch
        when (val result = updateGroupNotificationUseCase(groupId, enabled)) {
            is MoilResult.Success -> Unit
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(error = result.error)
        }
    }

    /** 그룹 생성 화면에서 가입 때 저장한 이름을 닉네임으로 사용해 서버에 그룹을 생성합니다. */
    fun createGroup(name: String, color: GroupColor) = viewModelScope.launch {
        val nickname = sessionManager.currentUserName()

        if (nickname == null) {
            mutableUiState.value = mutableUiState.value.copy(
                isCurrentUserNameMissing = true,
                error = null,
            )
            return@launch
        }

        when (val result = createGroupUseCase(name, nickname, color)) {
            is MoilResult.Success -> loadGroups(preferredGroupId = result.value.id)
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(error = result.error)
        }
    }

    // 초대 코드 확인 이벤트에서 그룹 정보를 검증한 뒤 실제 구성원 프로필까지 함께 불러옵니다.
    // 두 요청이 모두 성공한 경우에만 가입 프로필 설정 화면으로 이동할 수 있는 상태를 공개합니다.
    fun verifyInvite(inviteCode: String) = viewModelScope.launch {
        mutableUiState.value = mutableUiState.value.copy(
            inviteVerification = null,
            joinGroupMembers = emptyList(),
            error = null,
        )

        when (val result = verifyInviteUseCase(inviteCode)) {
            is MoilResult.Success -> loadJoinGroupProfile(result.value)
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(
                error = result.error,
            )
        }
    }

    // 초대 검증 성공 뒤 호출되어 그룹 상세의 실제 구성원 목록을 가입 프로필 UI 상태로 제공합니다.
    private suspend fun loadJoinGroupProfile(inviteVerification: InviteVerification) {
        when (val result = getGroupUseCase(inviteVerification.groupId)) {
            is MoilResult.Success -> mutableUiState.value = mutableUiState.value.copy(
                inviteVerification = inviteVerification,
                joinGroupMembers = result.value.members,
                error = null,
            )
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(
                error = result.error,
            )
        }
    }

    fun joinGroup(inviteCode: String, nickname: String, color: GroupColor) = viewModelScope.launch {
        when (val result = joinGroupUseCase(inviteCode, nickname, color)) {
            is MoilResult.Success -> loadGroups(preferredGroupId = result.value.id)
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(error = result.error)
        }
    }

    fun renameSelectedGroup(name: String) = viewModelScope.launch {
        val groupId = mutableUiState.value.selectedGroupId ?: return@launch
        when (val result = renameGroupUseCase(groupId, name)) {
            is MoilResult.Success -> loadGroups()
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(error = result.error)
        }
    }

    fun updateSelectedMemberRoles(roles: Map<Long, GroupRole>) = viewModelScope.launch {
        val groupId = mutableUiState.value.selectedGroupId ?: return@launch
        when (val result = updateMemberRolesUseCase(groupId, roles)) {
            is MoilResult.Success -> loadMembers(groupId)
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(error = result.error)
        }
    }

    fun transferAdmin(targetUserId: Long) = viewModelScope.launch {
        val groupId = mutableUiState.value.selectedGroupId ?: return@launch
        when (val result = transferAdminUseCase(groupId, targetUserId)) {
            is MoilResult.Success -> selectGroup(groupId)
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(error = result.error)
        }
    }

    fun leaveSelectedGroup() = viewModelScope.launch {
        val groupId = mutableUiState.value.selectedGroupId ?: return@launch
        when (val result = leaveGroupUseCase(groupId)) {
            is MoilResult.Success -> loadGroups()
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(error = result.error)
        }
    }

    private fun loadMembers(groupId: Long) = viewModelScope.launch {
        when (val result = getGroupMembersUseCase(groupId)) {
            is MoilResult.Success -> mutableUiState.value = mutableUiState.value.copy(members = result.value)
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(error = result.error)
        }
    }

    private fun loadGroupDetail(groupId: Long) = viewModelScope.launch {
        when (val result = getGroupUseCase(groupId)) {
            is MoilResult.Success -> mutableUiState.value = mutableUiState.value.copy(selectedGroupDetail = result.value)
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(error = result.error)
        }
    }
}
