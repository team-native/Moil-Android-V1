package com.example.moil.feature.group.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.feature.group.viewmodel.CreateGroupScreenEvent
import com.example.moil.feature.group.viewmodel.CreateGroupUiState
import com.example.moil.ui.theme.MoilGroupCreateDimension
import com.example.moil.ui.theme.MoilTheme

@Composable
internal fun CreateGroupScreenContent(
    uiState: CreateGroupUiState,
    onEvent: (CreateGroupScreenEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = MoilGroupCreateDimension.ScreenHorizontalPadding),
        verticalArrangement = Arrangement.Top,
    ) {
        CreateGroupHeader(
            onBackClick = { onEvent(CreateGroupScreenEvent.BackClicked) },
        )

        Spacer(modifier = Modifier.height(MoilGroupCreateDimension.LabelTopPadding))

        GroupNameTextField(
            value = uiState.groupName,
            groupNameError = uiState.groupNameError,
            onValueChange = { groupName ->
                onEvent(CreateGroupScreenEvent.GroupNameChanged(groupName))
            },
        )

        Spacer(modifier = Modifier.height(MoilGroupCreateDimension.LabelTopPadding))

        ProfileAvatarSelector(
            selectedProfileAvatarRes = uiState.selectedProfileAvatarRes,
            selectedProfileImageUri = uiState.selectedProfileImageUri,
            onProfileAvatarSelected = { avatarRes ->
                onEvent(CreateGroupScreenEvent.ProfileAvatarSelected(avatarRes))
            },
            onCustomProfileImageClick = {
                onEvent(CreateGroupScreenEvent.CustomProfileImageClicked)
            },
        )

        Spacer(modifier = Modifier.weight(1f))

        CreateGroupActionButton(
            enabled = uiState.groupName.isNotBlank(),
            onClick = { onEvent(CreateGroupScreenEvent.CreateGroupClicked) },
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun CreateGroupScreenContentPreview() {
    MoilTheme(darkTheme = true) {
        CreateGroupScreenContent(
            uiState = CreateGroupUiState(),
            onEvent = {},
        )
    }
}
