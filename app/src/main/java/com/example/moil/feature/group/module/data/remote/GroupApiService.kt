package com.example.moil.feature.group.module.data.remote

import com.example.moil.core.network.ApiEnvelopeDto
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
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface GroupApiService {
    @POST("groups/me")
    suspend fun getMyGroups(): Response<ApiEnvelopeDto<List<GroupSummaryResponseDto>>>

    @POST("groups")
    suspend fun createGroup(@Body request: CreateGroupRequestDto): Response<ApiEnvelopeDto<GroupSummaryResponseDto>>

    @POST("groups/join/verify")
    suspend fun verifyInvite(@Body request: VerifyInviteRequestDto): Response<ApiEnvelopeDto<InviteVerificationResponseDto>>

    @POST("groups/join")
    suspend fun joinGroup(@Body request: JoinGroupRequestDto): Response<ApiEnvelopeDto<GroupSummaryResponseDto>>

    @GET("groups/{groupId}")
    suspend fun getGroup(@Path("groupId") groupId: Long): Response<ApiEnvelopeDto<GroupDetailResponseDto>>

    @DELETE("groups/{groupId}/members/me")
    suspend fun leaveGroup(@Path("groupId") groupId: Long): Response<ApiEnvelopeDto<Unit>>

    @PATCH("groups/{groupId}/members/me")
    suspend fun updateMyGroupProfile(
        @Path("groupId") groupId: Long,
        @Body request: UpdateMyGroupProfileRequestDto,
    ): Response<ApiEnvelopeDto<GroupMemberProfileResponseDto>>

    @GET("groups/{groupId}/members")
    suspend fun getMembers(@Path("groupId") groupId: Long): Response<ApiEnvelopeDto<List<GroupMemberResponseDto>>>

    @PATCH("groups/{groupId}/notification")
    suspend fun updateNotification(@Path("groupId") groupId: Long, @Body request: NotificationRequestDto): Response<ApiEnvelopeDto<Unit>>

    @PATCH("groups/{groupId}")
    suspend fun renameGroup(@Path("groupId") groupId: Long, @Body request: RenameGroupRequestDto): Response<ApiEnvelopeDto<Unit>>

    @PATCH("groups/{groupId}/members")
    suspend fun updateMemberRoles(@Path("groupId") groupId: Long, @Body request: UpdateMemberRolesRequestDto): Response<ApiEnvelopeDto<Unit>>

    @POST("groups/{groupId}/transfer-admin")
    suspend fun transferAdmin(@Path("groupId") groupId: Long, @Body request: TransferAdminRequestDto): Response<ApiEnvelopeDto<Unit>>
}
