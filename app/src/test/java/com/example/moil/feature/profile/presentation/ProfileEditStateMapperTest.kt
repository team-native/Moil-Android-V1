package com.example.moil.feature.profile.presentation

import com.example.moil.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileEditStateMapperTest {
    @Test
    fun `편집 상태는 현재 마이페이지 프로필 값을 초기값으로 사용한다`() {
        val profileUiState = ProfileUiState(
            profileName = "홍길동",
            profileAvatarRes = R.drawable.family_avatar_mom,
        )

        val editUiState = profileUiState.toProfileEditUiState()

        assertEquals("홍길동", editUiState.profileName)
        assertEquals(R.drawable.family_avatar_mom, editUiState.selectedProfileAvatarRes)
    }

    @Test
    fun `저장은 이름 공백을 제거하고 선택한 아바타만 마이페이지 상태에 반영한다`() {
        val currentProfile = ProfileUiState(
            isDarkTheme = true,
            profileName = "나",
            profileAvatarRes = R.drawable.family_avatar_member_green,
        )
        val editUiState = ProfileEditUiState(
            profileName = "  새 이름  ",
            selectedProfileAvatarRes = R.drawable.family_avatar_sibling,
        )

        val updatedProfile = editUiState.toUpdatedProfileUiState(currentProfile)

        assertEquals("새 이름", updatedProfile.profileName)
        assertEquals(R.drawable.family_avatar_sibling, updatedProfile.profileAvatarRes)
        assertTrue(updatedProfile.isDarkTheme)
    }

    @Test
    fun `공백 이름일 때 저장 버튼은 비활성화된다`() {
        assertFalse(ProfileEditUiState(profileName = "   ").canSave)
        assertTrue(ProfileEditUiState(profileName = "나").canSave)
    }
}
