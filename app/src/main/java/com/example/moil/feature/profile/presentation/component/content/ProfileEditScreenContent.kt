package com.example.moil.feature.profile.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.feature.family.presentation.FamilyDetailHeader
import com.example.moil.feature.group.presentation.ProfileAvatarSelector
import com.example.moil.feature.group.presentation.profileAvatarResources
import com.example.moil.ui.theme.MoilProfileEditDimension
import com.example.moil.ui.theme.MoilTheme

@Composable
internal fun ProfileEditScreenContent(
    uiState: ProfileEditUiState,
    onEvent: (ProfileEditScreenEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = MoilProfileEditDimension.ScreenHorizontalPadding),
    ) {
        FamilyDetailHeader(
            groupName = stringResource(R.string.profile_edit_title),
            onBackClick = { onEvent(ProfileEditScreenEvent.BackClicked) },
        )

        Spacer(modifier = Modifier.height(MoilProfileEditDimension.AvatarTopPadding))

        ProfileEditAvatarPreview(
            avatarRes = uiState.selectedProfileAvatarRes,
        )

        Spacer(modifier = Modifier.height(MoilProfileEditDimension.NameLabelTopPadding))

        ProfileEditNameField(
            profileName = uiState.profileName,
            onProfileNameChange = { profileName ->
                onEvent(ProfileEditScreenEvent.NameChanged(profileName))
            },
        )

        Spacer(modifier = Modifier.height(MoilProfileEditDimension.AvatarSelectorTopPadding))

        ProfileAvatarSelector(
            labelRes = R.string.profile_edit_avatar_label,
            selectedProfileAvatarRes = uiState.selectedProfileAvatarRes,
            onProfileAvatarSelected = { avatarRes ->
                onEvent(ProfileEditScreenEvent.ProfileAvatarSelected(avatarRes))
            },
            avatarResources = profileAvatarResources,
        )

        uiState.saveError?.let { saveError ->
            Spacer(modifier = Modifier.height(MoilProfileEditDimension.NameLabelTopPadding))

            ProfileEditSaveErrorText(error = saveError)
        }

        Spacer(modifier = Modifier.weight(1f))

        ProfileEditSaveButton(
            enabled = uiState.canSave,
            onClick = { onEvent(ProfileEditScreenEvent.SaveClicked) },
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun ProfileEditScreenContentPreview() {
    MoilTheme(darkTheme = false) {
        ProfileEditScreenContent(
            uiState = ProfileEditUiState(),
            onEvent = {},
        )
    }
}
