package com.example.moil.feature.group.presentation

import com.example.moil.core.component.MoilNavigationDestination

sealed interface JoinGroupScreenEvent {
    data class DestinationClicked(val destination: MoilNavigationDestination) : JoinGroupScreenEvent
    data class InviteCodeChanged(val inviteCode: String) : JoinGroupScreenEvent
    data object InviteCodeConfirmed : JoinGroupScreenEvent
    data object ProfileSetupBackClicked : JoinGroupScreenEvent
    data class ProfileNameChanged(val profileName: String) : JoinGroupScreenEvent
    data class ProfileAvatarSelected(val avatarRes: Int) : JoinGroupScreenEvent
    data object JoinGroupConfirmed : JoinGroupScreenEvent
}
