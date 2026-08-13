package com.example.moil.feature.group.domain

import com.example.moil.core.domain.MoilResult

interface GroupRepository {
    suspend fun getMyGroups(): MoilResult<List<GroupSummary>>
    suspend fun createGroup(name: String, nickname: String, color: GroupColor): MoilResult<GroupSummary>
    suspend fun verifyInvite(inviteCode: String): MoilResult<InviteVerification>
    suspend fun joinGroup(inviteCode: String, nickname: String, color: GroupColor): MoilResult<GroupSummary>
    suspend fun getGroup(groupId: Long): MoilResult<GroupDetail>
    suspend fun getMembers(groupId: Long): MoilResult<List<GroupMember>>
    suspend fun leaveGroup(groupId: Long): MoilResult<Unit>
    suspend fun updateMyGroupProfile(groupId: Long, nickname: String, color: GroupColor): MoilResult<GroupMemberProfile>
    suspend fun updateNotification(groupId: Long, enabled: Boolean): MoilResult<Unit>
    suspend fun renameGroup(groupId: Long, name: String): MoilResult<Unit>
    suspend fun updateMemberRoles(groupId: Long, roles: Map<Long, GroupRole>): MoilResult<Unit>
    suspend fun transferAdmin(groupId: Long, targetUserId: Long): MoilResult<Unit>
}
