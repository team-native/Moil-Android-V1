package com.example.moil.feature.profile.presentation

import androidx.annotation.DrawableRes
import com.example.moil.R

data class ProfileEditUiState(
    val profileName: String = "나",
    @param:DrawableRes val selectedProfileAvatarRes: Int = R.drawable.family_avatar_member_green,
    val isSaving: Boolean = false,
    val saveError: ProfileEditSaveError? = null,
) {
    val canSave: Boolean
        get() = profileName.isNotBlank() && !isSaving
}

enum class ProfileEditSaveError {
    SaveFailed,
}
