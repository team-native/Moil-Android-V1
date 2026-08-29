package com.example.moil.feature.group.viewmodel

import androidx.annotation.DrawableRes
import com.example.moil.R
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.GroupMember

private val selectableGroupColors = listOf(
    GroupColor.Sky,
    GroupColor.Red,
    GroupColor.Green,
    GroupColor.Yellow,
    GroupColor.Teal,
    GroupColor.Violet,
    GroupColor.Magenta,
)

data class JoinGroupProfileOptions(
    val usedProfiles: List<JoinGroupUsedProfileUiModel>,
    val availableColors: List<GroupColor>,
)

fun GroupMember.toJoinGroupUsedProfileUiModel(): JoinGroupUsedProfileUiModel =
    JoinGroupUsedProfileUiModel(
        nickname = nickname,
        color = color,
        imagePath = imagePath,
    )

fun List<GroupMember>.toJoinGroupProfileOptions(): JoinGroupProfileOptions {
    val usedProfiles = map(GroupMember::toJoinGroupUsedProfileUiModel)
    val usedColors = usedProfiles.map(JoinGroupUsedProfileUiModel::color).toSet()
    val availableColors = selectableGroupColors.filterNot(usedColors::contains)

    return JoinGroupProfileOptions(
        usedProfiles = usedProfiles,
        availableColors = availableColors,
    )
}

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
