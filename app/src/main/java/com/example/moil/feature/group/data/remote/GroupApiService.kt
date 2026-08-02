package com.example.moil.feature.group.data.remote

import com.example.moil.core.network.ApiEnvelopeDto
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
