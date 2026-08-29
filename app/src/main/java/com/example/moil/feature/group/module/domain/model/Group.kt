package com.example.moil.feature.group.module.domain.model

enum class GroupRole { Owner, Admin, Member, Unknown }

enum class GroupColor { Sky, Red, Green, Yellow, Teal, Violet, Magenta, Unknown }

data class GroupSummary(
    val id: Long,
    val name: String,
    val inviteCode: String?,
    val myRole: GroupRole,
    val myNickname: String?,
    val myColor: GroupColor,
    val memberCount: Int?,
    val myImagePath: String? = null,
)

data class GroupMember(
    val userId: Long,
    val nickname: String,
    val email: String?,
    val role: GroupRole,
    val color: GroupColor,
    val isMe: Boolean?,
    val imagePath: String? = null,
)

data class GroupMemberProfile(
    val groupId: Long,
    val userId: Long,
    val nickname: String,
    val color: GroupColor,
    val imagePath: String? = null,
)

data class InviteVerification(val groupId: Long, val groupName: String, val memberCount: Int, val inviteCode: String)

data class GroupDetail(
    val id: Long,
    val name: String,
    val inviteCode: String,
    val memberCount: Int,
    val monthlyEventCount: Int,
    val myRole: GroupRole,
    val members: List<GroupMember>,
)

fun String.toGroupRole(): GroupRole = when (uppercase()) {
    "OWNER" -> GroupRole.Owner
    "ADMIN" -> GroupRole.Admin
    "MEMBER" -> GroupRole.Member
    else -> GroupRole.Unknown
}

fun GroupRole.toWireValue(): String = when (this) {
    GroupRole.Owner -> "OWNER"
    GroupRole.Admin -> "ADMIN"
    GroupRole.Member -> "MEMBER"
    GroupRole.Unknown -> error("알 수 없는 역할은 권한 요청에 사용할 수 없습니다.")
}

fun String.toGroupColor(): GroupColor = when (uppercase()) {
    "SKY" -> GroupColor.Sky
    "RED" -> GroupColor.Red
    "GREEN" -> GroupColor.Green
    "YELLOW" -> GroupColor.Yellow
    "TEAL" -> GroupColor.Teal
    "VIOLET" -> GroupColor.Violet
    "MAGENTA" -> GroupColor.Magenta
    else -> GroupColor.Unknown
}

fun GroupColor.toWireValue(): String = when (this) {
    GroupColor.Sky -> "SKY"
    GroupColor.Red -> "RED"
    GroupColor.Green -> "GREEN"
    GroupColor.Yellow -> "YELLOW"
    GroupColor.Teal -> "TEAL"
    GroupColor.Violet -> "VIOLET"
    GroupColor.Magenta -> "MAGENTA"
    GroupColor.Unknown -> error("알 수 없는 색상은 서버에 전송할 수 없습니다.")
}
