package com.example.moil.feature.group.module.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupMemberProfileResponseDto(
    @SerialName("groupId") val groupId: Long,
    @SerialName("userId") val userId: Long,
    @SerialName("nickname") val nickname: String,
    @SerialName("colorId") val colorId: String? = null,
    @SerialName("imagePath") val imagePath: String? = null,
)

@Serializable
data class GroupSummaryResponseDto(
    @SerialName("groupId") val groupId: Long,
    @SerialName("name") val name: String,
    @SerialName("inviteCode") val inviteCode: String? = null,
    @SerialName("myRole") val myRole: String,
    @SerialName("myNickname") val myNickname: String? = null,
    @SerialName("myColor") val myColor: String? = null,
    @SerialName("myImagePath") val myImagePath: String? = null,
    @SerialName("memberCount") val memberCount: Int? = null,
)

@Serializable
data class GroupMemberResponseDto(
    @SerialName("userId") val userId: Long,
    @SerialName("nickname") val nickname: String,
    @SerialName("email") val email: String? = null,
    @SerialName("role") val role: String,
    @SerialName("colorId") val colorId: String? = null,
    @SerialName("imagePath") val imagePath: String? = null,
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
    @SerialName("colorId") val colorId: String? = null,
    @SerialName("imagePath") val imagePath: String? = null,
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

@Serializable
data class NotificationResponseDto(
    @SerialName("groupId") val groupId: Long,
    @SerialName("notificationEnabled") val notificationEnabled: Boolean,
)
