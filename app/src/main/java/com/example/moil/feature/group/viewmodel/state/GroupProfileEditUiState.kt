package com.example.moil.feature.group.viewmodel

import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.GroupMember
import com.example.moil.feature.group.module.domain.model.GroupSummary

/**
 * 특정 그룹 안에서 내 닉네임과 프로필(색 또는 사진)을 바꾸는 화면 상태다.
 *
 * 서버는 색과 사진 중 하나만 받는다(API 명세 "그룹 내 내 프로필 변경").
 * 새로 고른 사진은 [selectedProfileImageUri], 이미 서버에 있는 사진은 [currentImagePath]로 구분한다.
 */
data class GroupProfileEditUiState(
    val groupId: Long? = null,
    val groupName: String = "",
    val memberCount: Int = 0,
    val profileName: String = "",
    val usedProfiles: List<JoinGroupUsedProfileUiModel> = emptyList(),
    val availableProfileColors: List<GroupColor> = emptyList(),
    val selectedProfileColor: GroupColor? = null,
    val selectedProfileImageUri: String? = null,
    val currentImagePath: String? = null,
    val initialProfileName: String = "",
    val initialProfileColor: GroupColor? = null,
    val initialImagePath: String? = null,
    val isSaving: Boolean = false,
) {
    val isUsingImage: Boolean
        get() = selectedProfileColor == null && (selectedProfileImageUri != null || currentImagePath != null)

    val hasChanges: Boolean
        get() = profileName.trim() != initialProfileName ||
            selectedProfileImageUri != null ||
            selectedProfileColor != initialProfileColor ||
            currentImagePath != initialImagePath

    val canSave: Boolean
        get() = groupId != null &&
            profileName.isNotBlank() &&
            profileName.trim().length <= MAX_GROUP_NICKNAME_LENGTH &&
            (selectedProfileColor != null || isUsingImage) &&
            hasChanges &&
            !isSaving
}

/** 그룹 닉네임 최대 길이 (API 명세: 1자 이상 10자 이하). */
const val MAX_GROUP_NICKNAME_LENGTH = 10

private val editableGroupColors = listOf(
    GroupColor.Sky,
    GroupColor.Red,
    GroupColor.Green,
    GroupColor.Yellow,
    GroupColor.Teal,
    GroupColor.Violet,
    GroupColor.Magenta,
)

/**
 * 선택 그룹과 멤버 목록으로 내 그룹 프로필 편집 초기 상태를 만든다.
 * 다른 멤버가 쓰는 색은 고를 수 없고, 내 현재 색은 계속 고를 수 있다.
 */
fun GroupSummary.toGroupProfileEditUiState(members: List<GroupMember>): GroupProfileEditUiState {
    val myMember = members.firstOrNull { member -> member.isMe == true }
    val otherMembers = members.filterNot { member -> member.isMe == true }
    val usedColors = otherMembers.map(GroupMember::color).toSet()
    val myColor = (myMember?.color ?: myColor).takeIf { color -> color != GroupColor.Unknown }
    val myImagePath = myMember?.imagePath ?: myImagePath
    val myNickname = myMember?.nickname ?: myNickname.orEmpty()
    val initialColor = if (myImagePath != null) null else myColor

    return GroupProfileEditUiState(
        groupId = id,
        groupName = name,
        memberCount = memberCount ?: members.size,
        profileName = myNickname,
        usedProfiles = otherMembers.map(GroupMember::toJoinGroupUsedProfileUiModel),
        availableProfileColors = editableGroupColors.filter { color ->
            color == myColor || color !in usedColors
        },
        selectedProfileColor = initialColor,
        currentImagePath = myImagePath,
        initialProfileName = myNickname,
        initialProfileColor = initialColor,
        initialImagePath = myImagePath,
    )
}
