package com.example.moil.feature.group.module.data.mapper

import com.example.moil.feature.group.module.data.dto.GroupDetailResponseDto
import com.example.moil.feature.group.module.data.dto.GroupMemberProfileResponseDto
import com.example.moil.feature.group.module.data.dto.GroupMemberResponseDto
import com.example.moil.feature.group.module.data.dto.GroupSummaryResponseDto
import com.example.moil.feature.group.module.data.dto.MemberRoleRequestDto
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.GroupDetail
import com.example.moil.feature.group.module.domain.model.GroupMember
import com.example.moil.feature.group.module.domain.model.GroupMemberProfile
import com.example.moil.feature.group.module.domain.model.GroupRole
import com.example.moil.feature.group.module.domain.model.GroupSummary
import com.example.moil.feature.group.module.domain.model.toGroupColor
import com.example.moil.feature.group.module.domain.model.toGroupRole

internal fun GroupSummaryResponseDto.toDomain(): GroupSummary = GroupSummary(
    id = groupId,
    name = name,
    inviteCode = inviteCode,
    myRole = myRole.toGroupRole(),
    myNickname = myNickname,
    myColor = (myColor ?: "").toGroupColor(),
    memberCount = memberCount,
    myImagePath = myImagePath,
)

internal fun GroupMemberResponseDto.toDomain(): GroupMember = GroupMember(
    userId = userId,
    nickname = nickname,
    email = email,
    role = role.toGroupRole(),
    color = colorId?.toGroupColor() ?: GroupColor.Unknown,
    isMe = isMe,
    imagePath = imagePath,
)

internal fun GroupMemberProfileResponseDto.toDomain(): GroupMemberProfile = GroupMemberProfile(
    groupId = groupId,
    userId = userId,
    nickname = nickname,
    color = colorId?.toGroupColor() ?: GroupColor.Unknown,
    imagePath = imagePath,
)

internal fun GroupDetailResponseDto.toDomain(): GroupDetail = GroupDetail(
    id = groupId,
    name = name,
    inviteCode = inviteCode,
    memberCount = memberCount,
    monthlyEventCount = monthlyEventCount,
    myRole = myRole.toGroupRole(),
    members = members.map { member ->
        GroupMember(
            userId = member.userId,
            nickname = member.nickname,
            email = null,
            role = member.role.toGroupRole(),
            color = member.colorId?.toGroupColor()
                ?: GroupColor.Unknown,
            isMe = null,
            imagePath = member.imagePath,
        )
    },
    notificationEnabled = notificationEnabled,
)

// 권한 변경 API는 admin/member만 받으므로 그 밖의 역할은 null로 돌려 호출부가 실패로 처리하게 한다.
internal fun GroupRole.toMemberRoleRequestDtoOrNull(): MemberRoleRequestDto? = when (this) {
    GroupRole.Admin -> MemberRoleRequestDto.Admin
    GroupRole.Member -> MemberRoleRequestDto.Member
    GroupRole.Owner,
    GroupRole.Unknown,
    -> null
}
