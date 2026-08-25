package com.example.moil.feature.family.viewmodel

import com.example.moil.core.component.MoilNavigationDestination

sealed interface FamilyScreenEvent {
    data class DestinationClicked(val destination: MoilNavigationDestination) : FamilyScreenEvent
    data class GroupClicked(val groupId: String) : FamilyScreenEvent
    data class NotificationsChanged(val isEnabled: Boolean) : FamilyScreenEvent
    data object BackClicked : FamilyScreenEvent
    data object EmptyGroupJoinClicked : FamilyScreenEvent
    data object EmptyGroupCreateClicked : FamilyScreenEvent
    data object GroupNameChangeClicked : FamilyScreenEvent
    data object MemberPermissionsClicked : FamilyScreenEvent
    data object InviteCodeCopyClicked : FamilyScreenEvent
    data object InviteLinkShareClicked : FamilyScreenEvent
    data object AdministratorTransferClicked : FamilyScreenEvent
}
