package com.example.moil.feature.group.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
enum class MemberRoleRequestDto {
    @SerialName("admin")
    Admin,
    @SerialName("member")
    Member,
}

@Serializable
data class CreateGroupRequestDto(
    @SerialName("name") val name: String,
    @SerialName("nickname") val nickname: String,
    @SerialName("colorId") val colorId: String,
)

@Serializable
data class VerifyInviteRequestDto(
    @SerialName("inviteCode") val inviteCode: String,
)

@Serializable
data class JoinGroupRequestDto(
    @SerialName("inviteCode") val inviteCode: String,
    @SerialName("nickname") val nickname: String,
    @SerialName("colorId") val colorId: String,
)

@Serializable
data class NotificationRequestDto(
    @SerialName("enabled") val enabled: Boolean,
)

@Serializable
data class RenameGroupRequestDto(
    @SerialName("name") val name: String,
)

@Serializable
data class MemberRoleChangeDto(
    @SerialName("userId") val userId: Long,
    @SerialName("role") val role: MemberRoleRequestDto,
)

@Serializable
data class UpdateMemberRolesRequestDto(
    @SerialName("members") val members: List<MemberRoleChangeDto>,
)

@Serializable
data class TransferAdminRequestDto(
    @SerialName("targetUserId") val targetUserId: Long,
)

@Serializable
data class GroupSummaryResponseDto(
    @SerialName("groupId") val groupId: Long,
    @SerialName("name") val name: String,
    @SerialName("inviteCode") val inviteCode: String? = null,
    @SerialName("myRole") val myRole: String,
    @SerialName("myNickname") val myNickname: String? = null,
    @SerialName("myColor") val myColor: String? = null,
    @SerialName("memberCount") val memberCount: Int? = null,
)

@Serializable
data class GroupMemberResponseDto(
    @SerialName("userId") val userId: Long,
    @SerialName("nickname") val nickname: String,
    @SerialName("email") val email: String? = null,
    @SerialName("role") val role: String,
    @SerialName("colorId") val colorId: String,
    @SerialName("isMe") val isMe: Boolean? = null,
)

@Serializable
data class InviteVerificationResponseDto(
    @SerialName("groupId") val groupId: Long,
    @SerialName("name") val name: String,
    @SerialName("memberCount") val memberCount: Int,
    @SerialName("inviteCode") val inviteCode: String,
)

@Serializable
data class GroupDetailMemberResponseDto(
    @SerialName("userId") val userId: Long,
    @SerialName("nickname") val nickname: String,
    @SerialName("role") val role: String,
    @SerialName("colorId") val colorId: String,
)

@Serializable
data class GroupDetailResponseDto(
    @SerialName("groupId") val groupId: Long,
    @SerialName("name") val name: String,
    @SerialName("inviteCode") val inviteCode: String,
    @SerialName("memberCount") val memberCount: Int,
    @SerialName("monthlyEventCount") val monthlyEventCount: Int,
    @SerialName("myRole") val myRole: String,
    @SerialName("members") val members: List<GroupDetailMemberResponseDto>,
)
