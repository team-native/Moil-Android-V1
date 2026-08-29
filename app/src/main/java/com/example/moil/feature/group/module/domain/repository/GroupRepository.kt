package com.example.moil.feature.group.module.domain.repository

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.GroupDetail
import com.example.moil.feature.group.module.domain.model.GroupMember
import com.example.moil.feature.group.module.domain.model.GroupMemberProfile
import com.example.moil.feature.group.module.domain.model.GroupRole
import com.example.moil.feature.group.module.domain.model.GroupSummary
import com.example.moil.feature.group.module.domain.model.InviteVerification

interface GroupRepository {
    suspend fun getMyGroups(): MoilResult<List<GroupSummary>>
    suspend fun createGroup(name: String, nickname: String, color: GroupColor?, imagePath: String?): MoilResult<GroupSummary>
    suspend fun verifyInvite(inviteCode: String): MoilResult<InviteVerification>
    suspend fun joinGroup(inviteCode: String, nickname: String, color: GroupColor?, imagePath: String?): MoilResult<GroupSummary>
    suspend fun getGroup(groupId: Long): MoilResult<GroupDetail>
    suspend fun getMembers(groupId: Long): MoilResult<List<GroupMember>>
    suspend fun leaveGroup(groupId: Long): MoilResult<Unit>
    suspend fun updateMyGroupProfile(groupId: Long, nickname: String, color: GroupColor?, imagePath: String?): MoilResult<GroupMemberProfile>
    suspend fun updateNotification(groupId: Long, enabled: Boolean): MoilResult<Unit>
    suspend fun renameGroup(groupId: Long, name: String): MoilResult<Unit>
    suspend fun updateMemberRoles(groupId: Long, roles: Map<Long, GroupRole>): MoilResult<Unit>
    suspend fun transferAdmin(groupId: Long, targetUserId: Long): MoilResult<Unit>
}
