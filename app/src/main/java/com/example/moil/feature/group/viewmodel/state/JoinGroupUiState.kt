package com.example.moil.feature.group.viewmodel

import com.example.moil.feature.group.module.domain.model.GroupColor

data class JoinGroupUiState(
    val inviteCode: String = "",
    val step: JoinGroupStep = JoinGroupStep.InviteCode,
    val verifiedGroupName: String = "",
    val verifiedMemberCount: Int = 0,
    val profileName: String = "",
    val usedProfiles: List<JoinGroupUsedProfileUiModel> = emptyList(),
    val availableProfileColors: List<GroupColor> = emptyList(),
    val selectedProfileColor: GroupColor? = null,
)

data class JoinGroupUsedProfileUiModel(
    val nickname: String,
    val color: GroupColor,
    val isUsed: Boolean = true,
)

enum class JoinGroupStep {
    InviteCode,
    ProfileSetup,
}
