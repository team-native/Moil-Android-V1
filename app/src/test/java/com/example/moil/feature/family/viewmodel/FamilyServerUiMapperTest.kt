package com.example.moil.feature.family.viewmodel

import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.GroupMember
import com.example.moil.feature.group.module.domain.model.GroupRole
import com.example.moil.feature.group.module.domain.model.GroupSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class FamilyServerUiMapperTest {
    @Test
    fun `red server color maps to red family profile color`() {
        val groups = listOf(
            GroupSummary(
                id = 1L,
                name = "테스트",
                inviteCode = "invite-code",
                myRole = GroupRole.Admin,
                myNickname = "박지성",
                myColor = GroupColor.Red,
                memberCount = 1,
            ),
        )
        val members = listOf(
            GroupMember(
                userId = 1L,
                nickname = "박지성",
                email = null,
                role = GroupRole.Admin,
                color = GroupColor.Red,
                isMe = true,
            ),
        )

        val familyGroup = groups.toFamilyGroups(
            selectedGroupId = 1L,
            selectedGroupMembers = members,
        ).single()

        assertEquals(GroupColor.Red, familyGroup.profileColor)
        assertEquals(GroupColor.Red, familyGroup.members.single().profileColor)
    }
}
