package com.example.moil.feature.profile.presentation

import com.example.moil.core.component.MoilNavigationDestination

sealed interface ProfileScreenEvent {
    data class DestinationClicked(val destination: MoilNavigationDestination) : ProfileScreenEvent
    data class GroupClicked(val groupId: String) : ProfileScreenEvent
    data class DarkThemeChanged(val isDarkTheme: Boolean) : ProfileScreenEvent
    data object CreateGroupClicked : ProfileScreenEvent
}
