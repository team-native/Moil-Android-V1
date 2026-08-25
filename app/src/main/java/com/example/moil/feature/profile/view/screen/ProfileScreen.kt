package com.example.moil.feature.profile.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.feature.profile.viewmodel.ProfileGroupIndicator
import com.example.moil.feature.profile.viewmodel.ProfileGroupUiModel
import com.example.moil.feature.profile.viewmodel.ProfileScreenEvent
import com.example.moil.feature.profile.viewmodel.ProfileUiState
import com.example.moil.ui.theme.MoilTheme

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    groups: List<ProfileGroupUiModel>,
    onEvent: (ProfileScreenEvent) -> Unit,
) {
    ProfileScreenContent(
        uiState = uiState,
        groups = groups,
        onEvent = onEvent,
    )
}

@Preview(showBackground = true, heightDp = 844, widthDp = 390)
@Composable
private fun ProfileScreenPreview() {
    MoilTheme(darkTheme = false) {
        ProfileScreen(
            uiState = ProfileUiState(),
            groups = listOf(
                ProfileGroupUiModel(
                    id = "family",
                    name = stringResource(R.string.family_subtitle),
                    indicator = ProfileGroupIndicator.Primary,
                ),
                ProfileGroupUiModel(
                    id = "college",
                    name = stringResource(R.string.family_group_college),
                    indicator = ProfileGroupIndicator.Secondary,
                ),
                ProfileGroupUiModel(
                    id = "work",
                    name = stringResource(R.string.family_group_work),
                    indicator = ProfileGroupIndicator.Secondary,
                ),
            ),
            onEvent = {},
        )
    }
}
