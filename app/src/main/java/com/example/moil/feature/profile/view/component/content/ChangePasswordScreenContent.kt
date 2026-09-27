package com.example.moil.feature.profile.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.core.component.toUserMessage
import com.example.moil.feature.auth.view.AuthErrorText
import com.example.moil.feature.auth.view.AuthFieldLabel
import com.example.moil.feature.auth.view.AuthTextField
import com.example.moil.feature.family.view.FamilyDetailHeader
import com.example.moil.feature.profile.viewmodel.ChangePasswordScreenEvent
import com.example.moil.feature.profile.viewmodel.ChangePasswordUiState
import com.example.moil.ui.theme.MoilAuthDimension
import com.example.moil.ui.theme.MoilProfileEditDimension
import com.example.moil.ui.theme.MoilTheme

/** 현재 비밀번호와 새 비밀번호(확인 포함)를 입력받는 비밀번호 변경 화면 본문이다. */
@Composable
internal fun ChangePasswordScreenContent(
    uiState: ChangePasswordUiState,
    onEvent: (ChangePasswordScreenEvent) -> Unit,
) {
    val resources = LocalResources.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = MoilProfileEditDimension.ScreenHorizontalPadding),
    ) {
        FamilyDetailHeader(
            groupName = stringResource(R.string.profile_change_password),
            onBackClick = { onEvent(ChangePasswordScreenEvent.BackClicked) },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(MoilProfileEditDimension.AvatarTopPadding))

            AuthFieldLabel(text = stringResource(R.string.account_change_password_current))
            AuthTextField(
                value = uiState.currentPassword,
                onValueChange = { password ->
                    onEvent(ChangePasswordScreenEvent.CurrentPasswordChanged(password))
                },
                placeholder = stringResource(R.string.account_change_password_current),
                isPassword = true,
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.SectionSpacing))

            AuthFieldLabel(text = stringResource(R.string.account_change_password_new))
            AuthTextField(
                value = uiState.newPassword,
                onValueChange = { password ->
                    onEvent(ChangePasswordScreenEvent.NewPasswordChanged(password))
                },
                placeholder = stringResource(R.string.account_change_password_new),
                isError = uiState.isNewPasswordTooShort || uiState.isNewPasswordSameAsCurrent,
                isPassword = true,
            )

            if (uiState.isNewPasswordTooShort) {
                AuthErrorText(text = stringResource(R.string.auth_password_length_error))
            } else if (uiState.isNewPasswordSameAsCurrent) {
                AuthErrorText(text = stringResource(R.string.account_change_password_same_error))
            }

            Spacer(modifier = Modifier.height(MoilAuthDimension.SectionSpacing))

            AuthFieldLabel(text = stringResource(R.string.account_change_password_new_confirm))
            AuthTextField(
                value = uiState.newPasswordConfirmation,
                onValueChange = { password ->
                    onEvent(ChangePasswordScreenEvent.NewPasswordConfirmationChanged(password))
                },
                placeholder = stringResource(R.string.auth_confirm_password_placeholder),
                isError = uiState.isConfirmationMismatched,
                isPassword = true,
            )

            if (uiState.isConfirmationMismatched) {
                AuthErrorText(text = stringResource(R.string.auth_password_mismatch_error))
            }

            uiState.error?.let { error ->
                AuthErrorText(text = error.toUserMessage(resources))
            }
        }

        AccountSubmitButton(
            text = stringResource(R.string.profile_change_password),
            enabled = uiState.canSave,
            isInProgress = uiState.isSaving,
            onClick = { onEvent(ChangePasswordScreenEvent.SaveClicked) },
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun ChangePasswordScreenContentPreview() {
    MoilTheme(darkTheme = true) {
        ChangePasswordScreenContent(
            uiState = ChangePasswordUiState(newPassword = "1234"),
            onEvent = {},
        )
    }
}
