package com.example.moil.feature.group.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GroupEffect {
    data class GroupOperationCompleted(val groupId: Long) : GroupEffect
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
            mutableUiState.value = mutableUiState.value.copy(
                isCurrentUserNameMissing = true,
                error = null,
            )
            return@launch
        }

        mutableUiState.value = mutableUiState.value.copy(
            isSubmitting = true,
            error = null,
        )

        when (val imageResult = uploadSelectedImage(selectedImageUri)) {
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(
                isSubmitting = false,
                error = imageResult.error,
            )
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
                is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(
                    isSubmitting = false,
                    error = result.error,
                )
            }
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

        mutableUiState.value = mutableUiState.value.copy(
            isSubmitting = true,
            error = null,
        )

        when (val imageResult = uploadSelectedImage(selectedImageUri)) {
            is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(
                isSubmitting = false,
                error = imageResult.error,
            )
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
                is MoilResult.Failure -> mutableUiState.value = mutableUiState.value.copy(
                    isSubmitting = false,
                    error = result.error,
                )
            }
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
