package com.example.moil.feature.group.data.remote

import com.example.moil.core.network.ApiExecutor
import com.example.moil.core.network.NetworkResult
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

    override suspend fun getMembers(groupId: Long): NetworkResult<List<GroupMemberResponseDto>> = apiExecutor.execute {
        groupApiService.getMembers(groupId)
    }

    override suspend fun updateNotification(groupId: Long, request: NotificationRequestDto): NetworkResult<Unit> = apiExecutor.executeVoid {
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
