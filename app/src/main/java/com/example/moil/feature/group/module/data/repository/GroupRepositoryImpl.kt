package com.example.moil.feature.group.module.data.repository

import com.example.moil.core.domain.MoilResult
import com.example.moil.core.domain.mapToDomain
import com.example.moil.feature.group.module.data.dto.CreateGroupRequestDto
import com.example.moil.feature.group.module.data.dto.GroupDetailResponseDto
import com.example.moil.feature.group.module.data.dto.GroupMemberResponseDto
import com.example.moil.feature.group.module.data.dto.GroupSummaryResponseDto
import com.example.moil.feature.group.module.data.dto.JoinGroupRequestDto
import com.example.moil.feature.group.module.data.dto.MemberRoleChangeDto
import com.example.moil.feature.group.module.data.dto.NotificationRequestDto
import com.example.moil.feature.group.module.data.dto.RenameGroupRequestDto
import com.example.moil.feature.group.module.data.dto.TransferAdminRequestDto
import com.example.moil.feature.group.module.data.dto.UpdateMemberRolesRequestDto
import com.example.moil.feature.group.module.data.dto.UpdateMyGroupProfileRequestDto
import com.example.moil.feature.group.module.data.dto.VerifyInviteRequestDto
import com.example.moil.feature.group.module.data.mapper.toDomain
import com.example.moil.feature.group.module.data.mapper.toMemberRoleRequestDto
import com.example.moil.feature.group.module.data.remote.GroupRemoteDataSource
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.GroupDetail
import com.example.moil.feature.group.module.domain.model.GroupMember
import com.example.moil.feature.group.module.domain.model.GroupMemberProfile
import com.example.moil.feature.group.module.domain.model.GroupRole
import com.example.moil.feature.group.module.domain.model.GroupSummary
import com.example.moil.feature.group.module.domain.model.InviteVerification
import com.example.moil.feature.group.module.domain.model.toWireValue
import com.example.moil.feature.group.module.domain.repository.GroupRepository
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val groupRemoteDataSource: GroupRemoteDataSource,
) : GroupRepository {
    override suspend fun getMyGroups(): MoilResult<List<GroupSummary>> = groupRemoteDataSource
        .getMyGroups()
        .mapToDomain { groups -> groups.map(GroupSummaryResponseDto::toDomain) }

    override suspend fun createGroup(name: String, nickname: String, color: GroupColor): MoilResult<GroupSummary> = groupRemoteDataSource
        .createGroup(CreateGroupRequestDto(name, nickname, color.toWireValue()))
        .mapToDomain(GroupSummaryResponseDto::toDomain)

    override suspend fun verifyInvite(inviteCode: String): MoilResult<InviteVerification> = groupRemoteDataSource
        .verifyInvite(VerifyInviteRequestDto(inviteCode))
        .mapToDomain { response -> InviteVerification(response.groupId, response.name, response.memberCount, response.inviteCode) }

    override suspend fun joinGroup(inviteCode: String, nickname: String, color: GroupColor): MoilResult<GroupSummary> = groupRemoteDataSource
        .joinGroup(JoinGroupRequestDto(inviteCode, nickname, color.toWireValue()))
        .mapToDomain(GroupSummaryResponseDto::toDomain)

    override suspend fun getGroup(groupId: Long): MoilResult<GroupDetail> = groupRemoteDataSource
        .getGroup(groupId)
        .mapToDomain(GroupDetailResponseDto::toDomain)

    override suspend fun getMembers(groupId: Long): MoilResult<List<GroupMember>> = groupRemoteDataSource
        .getMembers(groupId)
        .mapToDomain { members -> members.map(GroupMemberResponseDto::toDomain) }

    override suspend fun leaveGroup(groupId: Long): MoilResult<Unit> = groupRemoteDataSource
        .leaveGroup(groupId)
        .mapToDomain { Unit }

    // 그룹 내 프로필 편집 화면의 저장 요청에 사용할 내 멤버 프로필을 갱신합니다.
    override suspend fun updateMyGroupProfile(
        groupId: Long,
        nickname: String,
        color: GroupColor,
    ): MoilResult<GroupMemberProfile> = groupRemoteDataSource
        .updateMyGroupProfile(
            groupId = groupId,
            request = UpdateMyGroupProfileRequestDto(
                nickname = nickname,
                colorId = color.toWireValue(),
            ),
        )
        .mapToDomain { response -> response.toDomain() }

    override suspend fun updateNotification(groupId: Long, enabled: Boolean): MoilResult<Unit> = groupRemoteDataSource
        .updateNotification(groupId, NotificationRequestDto(enabled))
        .mapToDomain { Unit }

    override suspend fun renameGroup(groupId: Long, name: String): MoilResult<Unit> = groupRemoteDataSource
        .renameGroup(groupId, RenameGroupRequestDto(name))
        .mapToDomain { Unit }
    override suspend fun updateMemberRoles(groupId: Long, roles: Map<Long, GroupRole>): MoilResult<Unit> {
        val changes = roles.map { (userId, role) ->
            MemberRoleChangeDto(userId, role.toMemberRoleRequestDto())
        }
        return groupRemoteDataSource
            .updateMemberRoles(groupId, UpdateMemberRolesRequestDto(changes))
            .mapToDomain { Unit }
    }

    override suspend fun transferAdmin(groupId: Long, targetUserId: Long): MoilResult<Unit> = groupRemoteDataSource
        .transferAdmin(groupId, TransferAdminRequestDto(targetUserId))
        .mapToDomain { Unit }
}
