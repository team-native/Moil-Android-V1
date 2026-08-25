package com.example.moil.feature.group.domain

import com.example.moil.core.domain.MoilResult
import javax.inject.Inject

class GetMyGroupsUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(): MoilResult<List<GroupSummary>> = repository.getMyGroups()
}

class CreateGroupUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(name: String, nickname: String, color: GroupColor): MoilResult<GroupSummary> =
        repository.createGroup(name, nickname, color)
}

class VerifyInviteUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(inviteCode: String): MoilResult<InviteVerification> =
        repository.verifyInvite(inviteCode)
}

class JoinGroupUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(code: String, nickname: String, color: GroupColor): MoilResult<GroupSummary> =
        repository.joinGroup(code, nickname, color)
}

class GetGroupUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(groupId: Long): MoilResult<GroupDetail> = repository.getGroup(groupId)
}

class GetGroupMembersUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(groupId: Long): MoilResult<List<GroupMember>> = repository.getMembers(groupId)
}

class LeaveGroupUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(groupId: Long): MoilResult<Unit> = repository.leaveGroup(groupId)
}

class UpdateMyGroupProfileUseCase @Inject constructor(private val repository: GroupRepository) {
    // 그룹별 내 프로필 저장 이벤트에서 닉네임과 허용된 색상을 서버에 반영합니다.
    suspend operator fun invoke(groupId: Long, nickname: String, color: GroupColor): MoilResult<GroupMemberProfile> = repository.updateMyGroupProfile(groupId, nickname, color)
}

class UpdateGroupNotificationUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(groupId: Long, enabled: Boolean): MoilResult<Unit> =
        repository.updateNotification(groupId, enabled)
}

class RenameGroupUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(groupId: Long, name: String): MoilResult<Unit> = repository.renameGroup(groupId, name)
}

class UpdateMemberRolesUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(groupId: Long, roles: Map<Long, GroupRole>): MoilResult<Unit> =
        repository.updateMemberRoles(groupId, roles)
}

class TransferAdminUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(groupId: Long, userId: Long): MoilResult<Unit> =
        repository.transferAdmin(groupId, userId)
}
