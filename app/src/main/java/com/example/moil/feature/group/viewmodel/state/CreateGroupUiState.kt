package com.example.moil.feature.group.viewmodel

import androidx.annotation.DrawableRes
import com.example.moil.R

data class CreateGroupUiState(
    val groupName: String = "",
    @param:DrawableRes val selectedProfileAvatarRes: Int = R.drawable.family_avatar_mom,
    val groupNameError: CreateGroupNameError? = null,
)

enum class CreateGroupNameError {
    Duplicate,
    MissingUserName,
}
