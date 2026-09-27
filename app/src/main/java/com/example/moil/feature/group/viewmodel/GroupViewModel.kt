package com.example.moil.feature.group.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moil.core.domain.MoilError
import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.repository.CurrentUserProfileStore
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.GroupRole
import com.example.moil.feature.group.module.domain.model.InviteVerification
import com.example.moil.feature.group.module.domain.usecase.CreateGroupUseCase
import com.example.moil.feature.group.module.domain.usecase.GetGroupMembersUseCase
import com.example.moil.feature.group.module.domain.usecase.GetGroupUseCase
import com.example.moil.feature.group.module.domain.usecase.GetMyGroupsUseCase
import com.example.moil.feature.group.module.domain.usecase.JoinGroupUseCase
import com.example.moil.feature.group.module.domain.usecase.LeaveGroupUseCase
import com.example.moil.feature.group.module.domain.usecase.RenameGroupUseCase
import com.example.moil.feature.group.module.domain.usecase.TransferAdminUseCase
import com.example.moil.feature.group.module.domain.usecase.UpdateGroupNotificationUseCase
import com.example.moil.feature.group.module.domain.usecase.UpdateMemberRolesUseCase
import com.example.moil.feature.group.module.domain.usecase.VerifyInviteUseCase
import com.example.moil.feature.image.module.domain.usecase.UploadProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GroupEffect {
    /** 그룹 생성·가입이 끝나 캘린더 탭으로 이동해야 한다. */
    data class GroupOperationCompleted(val groupId: Long) : GroupEffect

    /** 그룹 이름 변경이 서버에 반영됐다. */
    data object GroupRenamed : GroupEffect

    /** 멤버 권한 변경이 서버에 반영됐다. */
    data object MemberRolesUpdated : GroupEffect

    /** 선택 그룹에서 나갔다. 그룹 상세 등 해당 그룹 화면을 닫아야 한다. */
    data class GroupLeft(
        val groupId: Long,
        val groupName: String,
    ) : GroupEffect

    /** 개별 작업이 실패했다. 화면 전체 오류가 아니라 한 번만 안내한다. */
    data class OperationFailed(val error: MoilError) : GroupEffect
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
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val currentUserProfileStore: CurrentUserProfileStore,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(GroupUiState())
    val uiState: StateFlow<GroupUiState> = mutableUiState.asStateFlow()
    private val mutableEffects = MutableSharedFlow<GroupEffect>()
    val effects: SharedFlow<GroupEffect> = mutableEffects.asSharedFlow()

    // 그룹을 빠르게 바꿀 때 이전 그룹의 늦은 응답이 현재 선택을 덮어쓰지 않도록 진행 중인 로드를 취소한다.
    private var selectedGroupLoadJob: Job? = null

    init {
        loadGroups()
    }

    /** 화면 진입과 재시도 이벤트에서 내 그룹 UseCase를 호출해 빈 상태·오류 상태를 분리합니다. */
    fun loadGroups(preferredGroupId: Long? = null) = viewModelScope.launch {
        mutableUiState.value = mutableUiState.value.copy(
            isLoading = true,
            isCurrentUserNameMissing = false,
            loadError = null,
        )

        when (val result = getMyGroupsUseCase()) {
            is MoilResult.Success -> {
                val selectedGroupId = preferredGroupId
                    ?.takeIf { candidateId -> result.value.any { group -> group.id == candidateId } }
                    ?: mutableUiState.value.selectedGroupId
                        ?.takeIf { candidateId -> result.value.any { group -> group.id == candidateId } }
                    ?: result.value.firstOrNull()?.id
                val isSelectionChanged = selectedGroupId != mutableUiState.value.selectedGroupId

                mutableUiState.value = mutableUiState.value.copy(
                    isLoading = false,
                    groups = result.value,
                    selectedGroupId = selectedGroupId,
                    members = if (selectedGroupId == null || isSelectionChanged) {
                        emptyList()
                    } else {
                        mutableUiState.value.members
                    },
                    selectedGroupDetail = if (selectedGroupId == null || isSelectionChanged) {
                        null
                    } else {
                        mutableUiState.value.selectedGroupDetail
                    },
                )

                if (selectedGroupId != null) {
                    loadSelectedGroup(selectedGroupId)
                }
            }

            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(
                    isLoading = false,
                    loadError = result.error,
                )
            }
        }
    }

    /** 그룹 선택 이벤트에서 선택 그룹의 상세와 멤버 목록을 새로 로드합니다. */
    fun selectGroup(groupId: Long) {
        mutableUiState.value = mutableUiState.value.copy(
            selectedGroupId = groupId,
            selectedGroupDetail = null,
            members = emptyList(),
            loadError = null,
        )
        loadSelectedGroup(groupId)
    }

    /**
     * 그룹 설정의 알림 스위치 이벤트에서 호출됩니다.
     * 성공하면 서버가 확정한 값으로 상세 상태를 갱신하고, 실패하면 이전 값을 유지한 채 실패만 알립니다.
     */
    fun updateNotification(enabled: Boolean) = viewModelScope.launch {
        val currentState = mutableUiState.value
        val groupId = currentState.selectedGroupId ?: return@launch

        if (currentState.isNotificationUpdating) {
            return@launch
        }

        mutableUiState.value = currentState.copy(isNotificationUpdating = true)

        when (val result = updateGroupNotificationUseCase(groupId, enabled)) {
            is MoilResult.Success -> {
                val latestState = mutableUiState.value
                val updatedDetail = latestState.selectedGroupDetail
                    ?.takeIf { detail -> detail.id == groupId }
                    ?.copy(notificationEnabled = result.value)

                mutableUiState.value = latestState.copy(
                    isNotificationUpdating = false,
                    selectedGroupDetail = updatedDetail ?: latestState.selectedGroupDetail,
                )
            }

            is MoilResult.Failure -> {
                mutableUiState.value = mutableUiState.value.copy(isNotificationUpdating = false)
                mutableEffects.emit(GroupEffect.OperationFailed(result.error))
            }
        }
    }

    /** 그룹 생성 화면의 선택 이미지를 먼저 업로드한 뒤 색상 또는 이미지 경로로 그룹을 생성합니다. */
    fun createGroup(
        name: String,
        color: GroupColor?,
        selectedImageUri: String?,
    ) = viewModelScope.launch {
        if (mutableUiState.value.isSubmitting) {
            return@launch
        }

        val nickname = currentUserProfileStore.profile.value?.name

        if (nickname == null) {
            mutableUiState.value = mutableUiState.value.copy(isCurrentUserNameMissing = true)
            return@launch
        }

        mutableUiState.value = mutableUiState.value.copy(isSubmitting = true)

        when (val imageResult = uploadSelectedImage(selectedImageUri)) {
            is MoilResult.Failure -> failSubmission(imageResult.error)

            is MoilResult.Success -> when (
                val result = createGroupUseCase(
                    name = name,
                    nickname = nickname,
                    color = if (imageResult.value == null) color else null,
                    imagePath = imageResult.value,
                )
            ) {
                is MoilResult.Success -> {
                    mutableUiState.value = mutableUiState.value.copy(isSubmitting = false)
                    loadGroups(preferredGroupId = result.value.id)
                    mutableEffects.emit(GroupEffect.GroupOperationCompleted(result.value.id))
                }

                is MoilResult.Failure -> failSubmission(result.error)
            }
        }
    }

    // 초대 코드 확인 이벤트에서 그룹 정보를 검증한 뒤 실제 구성원 프로필까지 함께 불러옵니다.
    // 두 요청이 모두 성공한 경우에만 가입 프로필 설정 화면으로 이동할 수 있는 상태를 공개합니다.
    fun verifyInvite(inviteCode: String) = viewModelScope.launch {
        mutableUiState.value = mutableUiState.value.copy(
            inviteVerification = null,
            joinGroupMembers = emptyList(),
        )

        when (val result = verifyInviteUseCase(inviteCode)) {
            is MoilResult.Success -> loadJoinGroupProfile(result.value)
            is MoilResult.Failure -> mutableEffects.emit(GroupEffect.OperationFailed(result.error))
        }
    }

    // 초대 검증 성공 뒤 호출되어 그룹 상세의 실제 구성원 목록을 가입 프로필 UI 상태로 제공합니다.
    private suspend fun loadJoinGroupProfile(inviteVerification: InviteVerification) {
        when (val result = getGroupUseCase(inviteVerification.groupId)) {
            is MoilResult.Success -> {
                mutableUiState.value = mutableUiState.value.copy(
                    inviteVerification = inviteVerification,
                    joinGroupMembers = result.value.members,
                )
            }

            is MoilResult.Failure -> mutableEffects.emit(GroupEffect.OperationFailed(result.error))
        }
    }

    /** 그룹 가입 프로필 이미지를 업로드한 뒤 가입 요청을 실행합니다. */
    fun joinGroup(
        inviteCode: String,
        nickname: String,
        color: GroupColor?,
        selectedImageUri: String?,
    ) = viewModelScope.launch {
        if (mutableUiState.value.isSubmitting) {
            return@launch
        }

        mutableUiState.value = mutableUiState.value.copy(isSubmitting = true)

        when (val imageResult = uploadSelectedImage(selectedImageUri)) {
            is MoilResult.Failure -> failSubmission(imageResult.error)

            is MoilResult.Success -> when (
                val result = joinGroupUseCase(
                    code = inviteCode,
                    nickname = nickname,
                    color = if (imageResult.value == null) color else null,
                    imagePath = imageResult.value,
                )
            ) {
                is MoilResult.Success -> {
                    mutableUiState.value = mutableUiState.value.copy(isSubmitting = false)
                    loadGroups(preferredGroupId = result.value.id)
                    mutableEffects.emit(GroupEffect.GroupOperationCompleted(result.value.id))
                }

                is MoilResult.Failure -> failSubmission(result.error)
            }
        }
    }

    /**
     * 그룹 이름 변경 다이얼로그의 저장 이벤트에서 호출됩니다.
     * 성공하면 목록을 다시 불러오고 다이얼로그를 닫도록 [GroupEffect.GroupRenamed]를 보냅니다.
     */
    fun renameSelectedGroup(name: String) = runManagementOperation { groupId ->
        when (val result = renameGroupUseCase(groupId, name)) {
            is MoilResult.Success -> {
                loadGroups(preferredGroupId = groupId)
                mutableEffects.emit(GroupEffect.GroupRenamed)
            }

            is MoilResult.Failure -> mutableEffects.emit(GroupEffect.OperationFailed(result.error))
        }
    }

    /**
     * 멤버 권한 설정 시트의 완료 이벤트에서 호출됩니다.
     * 현재 역할과 달라진 관리자/멤버 변경만 서버에 보내며, 변경이 없으면 요청 없이 완료로 처리합니다.
     * 서버가 관리하는 Owner와 알 수 없는 역할은 요청에서 제외합니다.
     */
    fun updateSelectedMemberRoles(requestedRoles: Map<Long, GroupRole>) = runManagementOperation { groupId ->
        val currentRoles = mutableUiState.value.members.associate { member -> member.userId to member.role }
        val changedRoles = requestedRoles.filter { (userId, requestedRole) ->
            val currentRole = currentRoles[userId]

            currentRole != null &&
                currentRole.isEditableRole() &&
                requestedRole.isEditableRole() &&
                currentRole != requestedRole
        }

        if (changedRoles.isEmpty()) {
            mutableEffects.emit(GroupEffect.MemberRolesUpdated)
            return@runManagementOperation
        }

        when (val result = updateMemberRolesUseCase(groupId, changedRoles)) {
            is MoilResult.Success -> {
                loadGroups(preferredGroupId = groupId)
                mutableEffects.emit(GroupEffect.MemberRolesUpdated)
            }

            is MoilResult.Failure -> mutableEffects.emit(GroupEffect.OperationFailed(result.error))
        }
    }

    /** 그룹 나가기 확인 다이얼로그에서 호출되어 선택 그룹에서 나갑니다. */
    fun leaveSelectedGroup() = runManagementOperation { groupId ->
        leaveGroup(groupId)
    }

    /**
     * 유일한 관리자가 그룹을 나갈 때 호출됩니다.
     * 관리자 권한을 먼저 넘기고 성공한 경우에만 나가기를 요청합니다.
     * 양도만 성공하고 나가기가 실패하면 일반 멤버로 남으므로 목록을 다시 불러와 역할을 갱신합니다.
     */
    fun transferAdminAndLeaveSelectedGroup(targetUserId: Long) = runManagementOperation { groupId ->
        when (val transferResult = transferAdminUseCase(groupId, targetUserId)) {
            is MoilResult.Success -> {
                val isLeft = leaveGroup(groupId)

                if (!isLeft) {
                    loadGroups(preferredGroupId = groupId)
                }
            }

            is MoilResult.Failure -> mutableEffects.emit(GroupEffect.OperationFailed(transferResult.error))
        }
    }

    // 나가기 요청을 보내고 결과 효과를 알린다. 성공 여부를 돌려줘 연속 작업이 후속 처리를 결정하게 한다.
    private suspend fun leaveGroup(groupId: Long): Boolean {
        val groupName = mutableUiState.value.groups
            .firstOrNull { group -> group.id == groupId }
            ?.name
            .orEmpty()

        return when (val result = leaveGroupUseCase(groupId)) {
            is MoilResult.Success -> {
                loadGroups()
                mutableEffects.emit(
                    GroupEffect.GroupLeft(
                        groupId = groupId,
                        groupName = groupName,
                    ),
                )
                true
            }

            is MoilResult.Failure -> {
                mutableEffects.emit(GroupEffect.OperationFailed(result.error))
                false
            }
        }
    }

    // 선택 그룹 관리 작업을 한 번에 하나만 실행하고, 끝나면 진행 상태를 해제한다.
    private fun runManagementOperation(
        operation: suspend (groupId: Long) -> Unit,
    ) = viewModelScope.launch {
        val currentState = mutableUiState.value
        val groupId = currentState.selectedGroupId ?: return@launch

        if (currentState.isManagementInProgress) {
            return@launch
        }

        mutableUiState.value = currentState.copy(isManagementInProgress = true)

        try {
            operation(groupId)
        } finally {
            mutableUiState.value = mutableUiState.value.copy(isManagementInProgress = false)
        }
    }

    // 생성·가입 제출 실패를 폼 진행 상태 해제와 일회성 안내로 처리한다.
    private suspend fun failSubmission(error: MoilError) {
        mutableUiState.value = mutableUiState.value.copy(isSubmitting = false)
        mutableEffects.emit(GroupEffect.OperationFailed(error))
    }

    // 선택 그룹의 상세와 멤버를 함께 불러오며, 응답이 도착했을 때 선택이 바뀌었으면 반영하지 않는다.
    private fun loadSelectedGroup(groupId: Long) {
        selectedGroupLoadJob?.cancel()
        selectedGroupLoadJob = viewModelScope.launch {
            launch { loadGroupDetail(groupId) }
            launch { loadMembers(groupId) }
        }
    }

    private suspend fun loadMembers(groupId: Long) {
        val result = getGroupMembersUseCase(groupId)

        if (mutableUiState.value.selectedGroupId != groupId) {
            return
        }

        mutableUiState.value = when (result) {
            is MoilResult.Success -> mutableUiState.value.copy(members = result.value)
            is MoilResult.Failure -> mutableUiState.value.copy(loadError = result.error)
        }
    }

    private suspend fun loadGroupDetail(groupId: Long) {
        val result = getGroupUseCase(groupId)

        if (mutableUiState.value.selectedGroupId != groupId) {
            return
        }

        mutableUiState.value = when (result) {
            is MoilResult.Success -> mutableUiState.value.copy(selectedGroupDetail = result.value)
            is MoilResult.Failure -> mutableUiState.value.copy(loadError = result.error)
        }
    }

    private suspend fun uploadSelectedImage(selectedImageUri: String?): MoilResult<String?> {
        if (selectedImageUri.isNullOrBlank()) {
            return MoilResult.Success(null)
        }

        return when (val result = uploadProfileImageUseCase(selectedImageUri)) {
            is MoilResult.Success -> MoilResult.Success(result.value.imagePath)
            is MoilResult.Failure -> MoilResult.Failure(result.error)
        }
    }
}

// 권한 변경 API가 다룰 수 있는 역할인지 판단한다.
private fun GroupRole.isEditableRole(): Boolean = this == GroupRole.Admin || this == GroupRole.Member
