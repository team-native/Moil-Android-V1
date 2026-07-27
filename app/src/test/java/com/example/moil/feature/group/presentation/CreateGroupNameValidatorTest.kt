package com.example.moil.feature.group.presentation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateGroupNameValidatorTest {
    @Test
    fun `같은 이름은 앞뒤 공백과 대소문자를 무시하고 중복으로 판단한다`() {
        val isDuplicate = CreateGroupNameValidator.isDuplicate(
            groupName = "  우리 가족  ",
            existingGroupNames = listOf("우리 가족", "대학 동기"),
        )

        assertTrue(isDuplicate)
    }

    @Test
    fun `새 이름은 중복이 아니다`() {
        val isDuplicate = CreateGroupNameValidator.isDuplicate(
            groupName = "주말 모임",
            existingGroupNames = listOf("우리 가족", "대학 동기"),
        )

        assertFalse(isDuplicate)
    }
}
