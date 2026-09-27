package com.example.moil.feature.profile.viewmodel


sealed interface ProfileEditScreenEvent {
    data object BackClicked : ProfileEditScreenEvent
    data class NameChanged(val profileName: String) : ProfileEditScreenEvent
    data object SaveClicked : ProfileEditScreenEvent
}
