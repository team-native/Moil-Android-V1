package com.example.moil.feature.group.module.data.mapper

import com.example.moil.feature.group.module.data.dto.GroupDetailMemberResponseDto
import com.example.moil.feature.group.module.data.dto.GroupDetailResponseDto
import com.example.moil.feature.group.module.data.dto.GroupMemberProfileResponseDto
import com.example.moil.feature.group.module.data.dto.GroupMemberResponseDto
import com.example.moil.feature.group.module.data.dto.GroupSummaryResponseDto
import com.example.moil.feature.group.module.domain.model.GroupColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GroupMapperContractTest {
    @Test
    fun `그룹 요약 응답의 내 이미지 경로를 domain에 보존한다`() {
        val groupSummary = GroupSummaryResponseDto(
            groupId = 1L,
            name = "우리 가족",
            inviteCode = "FAM-1",
            myRole = "member",
            myNickname = "모일",
            myColor = "SKY",
            myImagePath = "/image/me",
            memberCount = 2,
        ).toDomain()

        assertEquals("/image/me", groupSummary.myImagePath)
    }

    @Test
    fun `nullable colorId와 imagePath를 멤버 domain에 보존한다`() {
        val member = GroupMemberResponseDto(
            userId = 2L,
            nickname = "모일",
            email = "moil@example.com",
            role = "member",
            colorId = null,
            imagePath = "/image/member",
            isMe = true,
        ).toDomain()

        assertEquals(GroupColor.Unknown, member.color)
        assertEquals("/image/member", member.imagePath)
    }

    @Test
    fun `그룹 상세 멤버는 color가 아닌 colorId 응답 키를 사용한다`() {
        val groupDetail = GroupDetailResponseDto(
            groupId = 1L,
            name = "우리 가족",
            inviteCode = "FAM-1",
            memberCount = 1,
            monthlyEventCount = 0,
            myRole = "admin",
            members = listOf(
                GroupDetailMemberResponseDto(
                    userId = 1L,
                    nickname = "모일",
                    role = "admin",
                    colorId = "RED",
                    imagePath = null,
                ),
            ),
        ).toDomain()

        assertEquals(GroupColor.Red, groupDetail.members.single().color)
        assertNull(groupDetail.members.single().imagePath)
    }

    @Test
    fun `내 그룹 프로필 응답의 nullable colorId와 imagePath를 domain에 매핑한다`() {
        val profile = GroupMemberProfileResponseDto(
            groupId = 1L,
            userId = 1L,
            nickname = "모일",
            colorId = null,
            imagePath = "/image/profile",
        ).toDomain()

        assertEquals(GroupColor.Unknown, profile.color)
        assertEquals("/image/profile", profile.imagePath)
    }
}
