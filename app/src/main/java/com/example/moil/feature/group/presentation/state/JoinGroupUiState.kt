package com.example.moil.feature.group.presentation

import androidx.annotation.DrawableRes
import com.example.moil.R

data class JoinGroupUiState(
    val inviteCode: String = "",
    val step: JoinGroupStep = JoinGroupStep.InviteCode,
    val verifiedGroupName: String = "",
    val verifiedMemberCount: Int = 0,
    val profileName: String = "",
    @param:DrawableRes val selectedProfileAvatarRes: Int = R.drawable.family_avatar_mine,
)

enum class JoinGroupStep {
    InviteCode,
    ProfileSetup,
}
