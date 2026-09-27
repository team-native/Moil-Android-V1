package com.example.moil.feature.group.viewmodel

import com.example.moil.feature.group.module.domain.model.GroupColor

sealed interface GroupProfileEditScreenEvent {
    data object BackClicked : GroupProfileEditScreenEvent
    data class ProfileNameChanged(val profileName: String) : GroupProfileEditScreenEvent
    data class ProfileColorSelected(val color: GroupColor) : GroupProfileEditScreenEvent
    data object CustomProfileImageClicked : GroupProfileEditScreenEvent
    data object SaveClicked : GroupProfileEditScreenEvent
}
