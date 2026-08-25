package com.example.moil.feature.group.module.data.dto

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
data class UpdateMyGroupProfileRequestDto(
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
