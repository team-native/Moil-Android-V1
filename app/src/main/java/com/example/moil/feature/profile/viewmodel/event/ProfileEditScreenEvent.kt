package com.example.moil.feature.profile.viewmodel

import androidx.annotation.DrawableRes

sealed interface ProfileEditScreenEvent {
    data object BackClicked : ProfileEditScreenEvent
    data class NameChanged(val profileName: String) : ProfileEditScreenEvent
    data class ProfileAvatarSelected(@param:DrawableRes val avatarRes: Int) : ProfileEditScreenEvent
    data object SaveClicked : ProfileEditScreenEvent
}
