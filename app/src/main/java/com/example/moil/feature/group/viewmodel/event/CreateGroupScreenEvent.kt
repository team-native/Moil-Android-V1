package com.example.moil.feature.group.viewmodel

import androidx.annotation.DrawableRes

sealed interface CreateGroupScreenEvent {
    data object BackClicked : CreateGroupScreenEvent
    data class GroupNameChanged(val groupName: String) : CreateGroupScreenEvent
    data class ProfileAvatarSelected(@param:DrawableRes val avatarRes: Int) : CreateGroupScreenEvent
    data object CustomProfileImageClicked : CreateGroupScreenEvent
    data object CreateGroupClicked : CreateGroupScreenEvent
}
