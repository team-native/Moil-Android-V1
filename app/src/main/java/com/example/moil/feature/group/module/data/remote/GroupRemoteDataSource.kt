package com.example.moil.feature.group.module.data.remote

import com.example.moil.core.network.NetworkResult
import com.example.moil.feature.group.module.data.dto.CreateGroupRequestDto
import com.example.moil.feature.group.module.data.dto.GroupDetailResponseDto
import com.example.moil.feature.group.module.data.dto.GroupMemberProfileResponseDto
import com.example.moil.feature.group.module.data.dto.GroupMemberResponseDto
import com.example.moil.feature.group.module.data.dto.GroupSummaryResponseDto
import com.example.moil.feature.group.module.data.dto.InviteVerificationResponseDto
import com.example.moil.feature.group.module.data.dto.JoinGroupRequestDto
import com.example.moil.feature.group.module.data.dto.NotificationRequestDto
import com.example.moil.feature.group.module.data.dto.RenameGroupRequestDto
import com.example.moil.feature.group.module.data.dto.TransferAdminRequestDto
import com.example.moil.feature.group.module.data.dto.UpdateMemberRolesRequestDto
import com.example.moil.feature.group.module.data.dto.UpdateMyGroupProfileRequestDto
import com.example.moil.feature.group.module.data.dto.VerifyInviteRequestDto

interface GroupRemoteDataSource {
    suspend fun getMyGroups(): NetworkResult<List<GroupSummaryResponseDto>>
    suspend fun createGroup(request: CreateGroupRequestDto): NetworkResult<GroupSummaryResponseDto>
    suspend fun verifyInvite(request: VerifyInviteRequestDto): NetworkResult<InviteVerificationResponseDto>
    suspend fun joinGroup(request: JoinGroupRequestDto): NetworkResult<GroupSummaryResponseDto>
    suspend fun getGroup(groupId: Long): NetworkResult<GroupDetailResponseDto>
    suspend fun leaveGroup(groupId: Long): NetworkResult<Unit>
    suspend fun updateMyGroupProfile(groupId: Long, request: UpdateMyGroupProfileRequestDto): NetworkResult<GroupMemberProfileResponseDto>
    suspend fun getMembers(groupId: Long): NetworkResult<List<GroupMemberResponseDto>>
    suspend fun updateNotification(groupId: Long, request: NotificationRequestDto): NetworkResult<Unit>
    suspend fun renameGroup(groupId: Long, request: RenameGroupRequestDto): NetworkResult<Unit>
    suspend fun updateMemberRoles(groupId: Long, request: UpdateMemberRolesRequestDto): NetworkResult<Unit>
    suspend fun transferAdmin(groupId: Long, request: TransferAdminRequestDto): NetworkResult<Unit>
}
