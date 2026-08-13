package com.example.moil.feature.group.data.remote

import com.example.moil.core.network.NetworkResult

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
