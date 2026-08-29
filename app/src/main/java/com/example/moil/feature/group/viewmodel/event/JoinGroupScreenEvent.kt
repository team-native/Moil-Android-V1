package com.example.moil.feature.group.viewmodel

import com.example.moil.core.component.MoilNavigationDestination
import com.example.moil.feature.group.module.domain.model.GroupColor

sealed interface JoinGroupScreenEvent {
    data class DestinationClicked(val destination: MoilNavigationDestination) : JoinGroupScreenEvent
    data class InviteCodeChanged(val inviteCode: String) : JoinGroupScreenEvent
    data object InviteCodeConfirmed : JoinGroupScreenEvent
    data object ProfileSetupBackClicked : JoinGroupScreenEvent
    data class ProfileNameChanged(val profileName: String) : JoinGroupScreenEvent
    data class ProfileColorSelected(val color: GroupColor) : JoinGroupScreenEvent
    data object CustomProfileImageClicked : JoinGroupScreenEvent
    data object JoinGroupConfirmed : JoinGroupScreenEvent
}
