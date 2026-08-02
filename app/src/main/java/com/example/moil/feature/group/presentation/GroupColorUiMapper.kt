package com.example.moil.feature.group.presentation

import androidx.annotation.DrawableRes
import com.example.moil.R
import com.example.moil.feature.group.domain.GroupColor

fun groupColorForAvatar(@DrawableRes avatarRes: Int): GroupColor = when (avatarRes) {
    R.drawable.family_avatar_mom -> GroupColor.Sky
    R.drawable.family_avatar_mine -> GroupColor.Red
    R.drawable.family_avatar_member_teal -> GroupColor.Green
    R.drawable.family_avatar_member_green -> GroupColor.Yellow
    R.drawable.family_avatar_member_blue -> GroupColor.Teal
    R.drawable.family_avatar_dad -> GroupColor.Violet
    R.drawable.family_avatar_sibling -> GroupColor.Magenta
    else -> GroupColor.Unknown
}
