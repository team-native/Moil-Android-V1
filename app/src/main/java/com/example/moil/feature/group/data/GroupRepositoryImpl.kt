package com.example.moil.feature.group.data

import com.example.moil.core.domain.MoilResult
import com.example.moil.core.domain.mapToDomain
import com.example.moil.feature.group.data.remote.CreateGroupRequestDto
import com.example.moil.feature.group.data.remote.GroupMemberResponseDto
import com.example.moil.feature.group.data.remote.GroupDetailResponseDto
import com.example.moil.feature.group.data.remote.GroupRemoteDataSource
import com.example.moil.feature.group.data.remote.GroupSummaryResponseDto
import com.example.moil.feature.group.data.remote.JoinGroupRequestDto
import com.example.moil.feature.group.data.remote.MemberRoleChangeDto
import com.example.moil.feature.group.data.remote.MemberRoleRequestDto
import com.example.moil.feature.group.data.remote.NotificationRequestDto
import com.example.moil.feature.group.data.remote.RenameGroupRequestDto
import com.example.moil.feature.group.data.remote.TransferAdminRequestDto
import com.example.moil.feature.group.data.remote.UpdateMemberRolesRequestDto
import com.example.moil.feature.group.data.remote.VerifyInviteRequestDto
import com.example.moil.feature.group.domain.GroupColor
import com.example.moil.feature.group.domain.GroupMember
import com.example.moil.feature.group.domain.GroupDetail
import com.example.moil.feature.group.domain.GroupRepository
import com.example.moil.feature.group.domain.GroupRole
import com.example.moil.feature.group.domain.GroupSummary
import com.example.moil.feature.group.domain.InviteVerification
import com.example.moil.feature.group.domain.toGroupColor
import com.example.moil.feature.group.domain.toGroupRole
import com.example.moil.feature.group.domain.toWireValue
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

private fun GroupSummaryResponseDto.toDomain(): GroupSummary = GroupSummary(
    id = groupId,
    name = name,
    inviteCode = inviteCode,
    myRole = myRole.toGroupRole(),
    myNickname = myNickname,
    myColor = (myColor ?: "").toGroupColor(),
    memberCount = memberCount,
)

private fun GroupMemberResponseDto.toDomain(): GroupMember = GroupMember(
    userId = userId,
    nickname = nickname,
    email = email,
    role = role.toGroupRole(),
    color = colorId.toGroupColor(),
    isMe = isMe,
)

private fun GroupDetailResponseDto.toDomain(): GroupDetail = GroupDetail(
    id = groupId,
    name = name,
    inviteCode = inviteCode,
    memberCount = memberCount,
    monthlyEventCount = monthlyEventCount,
    myRole = myRole.toGroupRole(),
    members = members.map { member ->
        GroupMember(
            userId = member.userId,
            nickname = member.nickname,
            email = null,
            role = member.role.toGroupRole(),
            color = member.colorId.toGroupColor(),
            isMe = null,
        )
    },
)

private fun GroupRole.toMemberRoleRequestDto(): MemberRoleRequestDto = when (this) {
    GroupRole.Admin -> MemberRoleRequestDto.Admin
    GroupRole.Member -> MemberRoleRequestDto.Member
    GroupRole.Owner,
    GroupRole.Unknown,
    -> error("관리자 또는 일반 멤버 역할만 변경 요청에 사용할 수 있습니다.")
}
