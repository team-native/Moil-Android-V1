package com.example.moil.feature.group.module.data.remote

import com.example.moil.core.network.ApiExecutor
import com.example.moil.core.network.NetworkResult
import com.example.moil.feature.group.module.data.dto.CreateGroupRequestDto
import com.example.moil.feature.group.module.data.dto.GroupDetailResponseDto
import com.example.moil.feature.group.module.data.dto.GroupMemberProfileResponseDto
import com.example.moil.feature.group.module.data.dto.GroupMemberResponseDto
import com.example.moil.feature.group.module.data.dto.GroupSummaryResponseDto
import com.example.moil.feature.group.module.data.dto.InviteVerificationResponseDto
import com.example.moil.feature.group.module.data.dto.JoinGroupRequestDto
import com.example.moil.feature.group.module.data.dto.NotificationRequestDto
import com.example.moil.feature.group.module.data.dto.NotificationResponseDto
import com.example.moil.feature.group.module.data.dto.RenameGroupRequestDto
import com.example.moil.feature.group.module.data.dto.TransferAdminRequestDto
import com.example.moil.feature.group.module.data.dto.UpdateMemberRolesRequestDto
import com.example.moil.feature.group.module.data.dto.UpdateMyGroupProfileRequestDto
import com.example.moil.feature.group.module.data.dto.VerifyInviteRequestDto
import javax.inject.Inject

class GroupRemoteDataSourceImpl @Inject constructor(
    private val groupApiService: GroupApiService,
    private val apiExecutor: ApiExecutor,
) : GroupRemoteDataSource {
    override suspend fun getMyGroups(): NetworkResult<List<GroupSummaryResponseDto>> = apiExecutor.execute {
        groupApiService.getMyGroups()
    }

    override suspend fun createGroup(request: CreateGroupRequestDto): NetworkResult<GroupSummaryResponseDto> = apiExecutor.execute {
        groupApiService.createGroup(request)
    }

    override suspend fun verifyInvite(request: VerifyInviteRequestDto): NetworkResult<InviteVerificationResponseDto> = apiExecutor.execute {
        groupApiService.verifyInvite(request)
    }

    override suspend fun joinGroup(request: JoinGroupRequestDto): NetworkResult<GroupSummaryResponseDto> = apiExecutor.execute {
        groupApiService.joinGroup(request)
    }

    override suspend fun getGroup(groupId: Long): NetworkResult<GroupDetailResponseDto> = apiExecutor.execute {
        groupApiService.getGroup(groupId)
    }

    override suspend fun leaveGroup(groupId: Long): NetworkResult<Unit> = apiExecutor.executeVoid {
        groupApiService.leaveGroup(groupId)
    }

    override suspend fun updateMyGroupProfile(
        groupId: Long,
        request: UpdateMyGroupProfileRequestDto,
    ): NetworkResult<GroupMemberProfileResponseDto> = apiExecutor.execute {
        groupApiService.updateMyGroupProfile(groupId, request)
    }

    override suspend fun getMembers(groupId: Long): NetworkResult<List<GroupMemberResponseDto>> = apiExecutor.execute {
        groupApiService.getMembers(groupId)
    }

    override suspend fun updateNotification(groupId: Long, request: NotificationRequestDto): NetworkResult<NotificationResponseDto> = apiExecutor.execute {
        groupApiService.updateNotification(groupId, request)
    }

    override suspend fun renameGroup(groupId: Long, request: RenameGroupRequestDto): NetworkResult<Unit> = apiExecutor.executeVoid {
        groupApiService.renameGroup(groupId, request)
    }

    override suspend fun updateMemberRoles(groupId: Long, request: UpdateMemberRolesRequestDto): NetworkResult<Unit> = apiExecutor.executeVoid {
        groupApiService.updateMemberRoles(groupId, request)
    }

    override suspend fun transferAdmin(groupId: Long, request: TransferAdminRequestDto): NetworkResult<Unit> = apiExecutor.executeVoid {
        groupApiService.transferAdmin(groupId, request)
    }
}
