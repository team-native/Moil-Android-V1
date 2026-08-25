package com.example.moil.feature.profile.viewmodel

import androidx.annotation.DrawableRes
import com.example.moil.R

data class ProfileUiState(
    val isDarkTheme: Boolean = false,
    val profileName: String = "나",
    @param:DrawableRes val profileAvatarRes: Int = R.drawable.family_avatar_member_green,
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
