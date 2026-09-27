package com.example.moil.feature.profile.viewmodel

import androidx.annotation.DrawableRes
import com.example.moil.R

data class ProfileUiState(
    val isDarkTheme: Boolean = false,
    // 서버 프로필 이름을 받기 전에는 임의 이름을 보여주지 않도록 비워 둔다.
    val profileName: String = "",
    @param:DrawableRes val profileAvatarRes: Int = R.drawable.family_avatar_member_green,
    // 소셜 로그인 계정은 비밀번호가 없어 비밀번호 변경 메뉴를 숨긴다.
    val canChangePassword: Boolean = true,
)

data class ProfileGroupUiModel(
    val id: String,
    val name: String,
    val indicator: ProfileGroupIndicator,
)

enum class ProfileGroupIndicator {
    Primary,
    Secondary,
}
