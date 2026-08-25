package com.example.moil.feature.group.module.data.mapper

import com.example.moil.feature.group.module.data.dto.GroupDetailResponseDto
import com.example.moil.feature.group.module.data.dto.GroupMemberProfileResponseDto
import com.example.moil.feature.group.module.data.dto.GroupMemberResponseDto
import com.example.moil.feature.group.module.data.dto.GroupSummaryResponseDto
import com.example.moil.feature.group.module.data.dto.MemberRoleRequestDto
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
)

internal fun GroupMemberResponseDto.toDomain(): GroupMember = GroupMember(
    userId = userId,
    nickname = nickname,
    email = email,
    role = role.toGroupRole(),
    color = colorId.toGroupColor(),
    isMe = isMe,
)

internal fun GroupMemberProfileResponseDto.toDomain(): GroupMemberProfile = GroupMemberProfile(
    groupId = groupId,
    userId = userId,
    nickname = nickname,
    color = colorId.toGroupColor(),
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
            color = member.colorId.toGroupColor(),
            isMe = null,
        )
    },
)

internal fun GroupRole.toMemberRoleRequestDto(): MemberRoleRequestDto = when (this) {
    GroupRole.Admin -> MemberRoleRequestDto.Admin
    GroupRole.Member -> MemberRoleRequestDto.Member
    GroupRole.Owner,
    GroupRole.Unknown,
    -> error("관리자 또는 일반 멤버 역할만 변경 요청에 사용할 수 있습니다.")
}
