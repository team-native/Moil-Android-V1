package com.example.moil.feature.group.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.core.component.MoilPrimaryButton
import com.example.moil.core.network.RemoteImageUrlResolver
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.viewmodel.GroupProfileEditScreenEvent
import com.example.moil.feature.group.viewmodel.GroupProfileEditUiState
import com.example.moil.feature.group.viewmodel.JoinGroupUsedProfileUiModel
import com.example.moil.feature.group.viewmodel.MAX_GROUP_NICKNAME_LENGTH
import com.example.moil.feature.group.viewmodel.avatarResourceForGroupColor
import com.example.moil.feature.group.viewmodel.groupColorForAvatar
import com.example.moil.ui.theme.LocalMoilExtraTypography
import com.example.moil.ui.theme.MoilGroupCreateDimension
import com.example.moil.ui.theme.MoilTheme

/**
 * 그룹 안에서 쓰는 내 닉네임과 프로필 색·사진을 바꾸는 화면 본문이다.
 * 가입 시 프로필 설정 화면과 같은 구성 요소를 재사용해 일관된 입력 경험을 준다.
 */
@Composable
internal fun GroupProfileEditScreenContent(
    uiState: GroupProfileEditUiState,
    onEvent: (GroupProfileEditScreenEvent) -> Unit,
) {
    val displayedImageUri = if (uiState.isUsingImage) {
        uiState.selectedProfileImageUri ?: RemoteImageUrlResolver.resolve(uiState.currentImagePath)
    } else {
        null
    }
    val isNameTooLong = uiState.profileName.trim().length > MAX_GROUP_NICKNAME_LENGTH

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = MoilGroupCreateDimension.ScreenHorizontalPadding),
    ) {
        GroupPageHeader(
            titleRes = R.string.group_profile_edit_title,
            onBackClick = { onEvent(GroupProfileEditScreenEvent.BackClicked) },
            titleStyle = LocalMoilExtraTypography.current.groupJoinTitle,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(MoilGroupCreateDimension.LabelTopPadding))

            JoinedGroupSummary(
                groupName = uiState.groupName,
                memberCount = uiState.memberCount,
            )

            Spacer(modifier = Modifier.height(MoilGroupCreateDimension.LabelTopPadding))

            ProfileNameField(
                profileName = uiState.profileName,
                onProfileNameChanged = { profileName ->
                    onEvent(GroupProfileEditScreenEvent.ProfileNameChanged(profileName))
                },
            )

            if (isNameTooLong) {
                Text(
                    text = stringResource(R.string.group_profile_edit_name_too_long, MAX_GROUP_NICKNAME_LENGTH),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Spacer(modifier = Modifier.height(MoilGroupCreateDimension.LabelTopPadding))

            UsedProfileList(profiles = uiState.usedProfiles)

            Spacer(modifier = Modifier.height(MoilGroupCreateDimension.LabelTopPadding))

            ProfileAvatarSelector(
                labelRes = R.string.group_join_profile_color_label,
                selectedProfileAvatarRes = uiState.selectedProfileColor?.let(::avatarResourceForGroupColor),
                selectedProfileImageUri = displayedImageUri,
                onProfileAvatarSelected = { avatarRes ->
                    onEvent(GroupProfileEditScreenEvent.ProfileColorSelected(groupColorForAvatar(avatarRes)))
                },
                onCustomProfileImageClick = {
                    onEvent(GroupProfileEditScreenEvent.CustomProfileImageClicked)
                },
                avatarResources = uiState.availableProfileColors.map(::avatarResourceForGroupColor),
            )
        }

        MoilPrimaryButton(
            text = stringResource(R.string.group_profile_edit_save),
            onClick = { onEvent(GroupProfileEditScreenEvent.SaveClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilGroupCreateDimension.BottomButtonHeight),
            enabled = uiState.canSave,
        )

        Spacer(modifier = Modifier.height(MoilGroupCreateDimension.BottomButtonPadding))
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun GroupProfileEditScreenContentPreview() {
    MoilTheme(darkTheme = true) {
        GroupProfileEditScreenContent(
            uiState = GroupProfileEditUiState(
                groupId = 1L,
                groupName = "우리 가족",
                memberCount = 3,
                profileName = "나",
                usedProfiles = listOf(
                    JoinGroupUsedProfileUiModel(
                        nickname = "엄마",
                        color = GroupColor.Red,
                    ),
                ),
                availableProfileColors = listOf(
                    GroupColor.Sky,
                    GroupColor.Green,
                ),
                selectedProfileColor = GroupColor.Green,
            ),
            onEvent = {},
        )
    }
}
