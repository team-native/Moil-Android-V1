package com.example.moil.feature.group.presentation

import androidx.annotation.DrawableRes
import com.example.moil.R
import com.example.moil.feature.group.domain.GroupColor

fun groupColorForAvatar(@DrawableRes avatarRes: Int): GroupColor = when (avatarRes) {
    R.drawable.family_avatar_mom -> GroupColor.Red
    R.drawable.family_avatar_mine -> GroupColor.Yellow
    R.drawable.family_avatar_member_teal -> GroupColor.Teal
    R.drawable.family_avatar_member_green -> GroupColor.Green
    R.drawable.family_avatar_member_blue -> GroupColor.Sky
    R.drawable.family_avatar_dad -> GroupColor.Violet
    R.drawable.family_avatar_sibling -> GroupColor.Magenta
    else -> GroupColor.Unknown
}

@DrawableRes
fun avatarResourceForGroupColor(groupColor: GroupColor): Int = when (groupColor) {
    GroupColor.Red -> R.drawable.family_avatar_mom
    GroupColor.Yellow -> R.drawable.family_avatar_mine
    GroupColor.Teal -> R.drawable.family_avatar_member_teal
    GroupColor.Green -> R.drawable.family_avatar_member_green
    GroupColor.Sky -> R.drawable.family_avatar_member_blue
    GroupColor.Violet -> R.drawable.family_avatar_dad
    GroupColor.Magenta -> R.drawable.family_avatar_sibling
    GroupColor.Unknown -> R.drawable.family_avatar_mine
}
