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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.core.component.toUserMessage
import com.example.moil.feature.auth.view.AuthErrorText
import com.example.moil.feature.auth.view.AuthFieldLabel
import com.example.moil.feature.auth.view.AuthTextField
import com.example.moil.feature.family.view.FamilyDetailHeader
import com.example.moil.feature.profile.viewmodel.DeleteAccountScreenEvent
import com.example.moil.feature.profile.viewmodel.DeleteAccountUiState
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilAuthDimension
import com.example.moil.ui.theme.MoilProfileEditDimension
import com.example.moil.ui.theme.MoilTheme

/**
 * 회원 탈퇴 화면 본문이다.
 * 이메일·비밀번호를 다시 입력받아 본인을 확인하고, 내가 쓴 일정을 남길지 고르게 한다.
 * 소셜 로그인 계정은 서버 탈퇴 API를 쓸 수 없어 안내 문구만 보여준다.
 */
@Composable
internal fun DeleteAccountScreenContent(
    uiState: DeleteAccountUiState,
    onEvent: (DeleteAccountScreenEvent) -> Unit,
) {
    val resources = LocalContext.current.resources
    val isInputEnabled = !uiState.isDeleting

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = MoilProfileEditDimension.ScreenHorizontalPadding),
    ) {
        FamilyDetailHeader(
            groupName = stringResource(R.string.profile_delete_account),
            onBackClick = { onEvent(DeleteAccountScreenEvent.BackClicked) },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(MoilProfileEditDimension.AvatarTopPadding))

            Text(
                text = stringResource(R.string.account_delete_title),
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(MoilAuthDimension.FieldSpacing))

            DeleteAccountNotice(text = stringResource(R.string.account_delete_notice_groups))

            DeleteAccountNotice(text = stringResource(R.string.account_delete_notice_irreversible))

            Spacer(modifier = Modifier.height(MoilAuthDimension.DescriptionBottomSpacing))

            if (uiState.isSocialAccount) {
                Text(
                    text = stringResource(R.string.account_delete_social_unsupported),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                AuthFieldLabel(text = stringResource(R.string.auth_email))
                AuthTextField(
                    value = uiState.email,
                    onValueChange = { email ->
                        onEvent(DeleteAccountScreenEvent.EmailChanged(email))
                    },
                    placeholder = stringResource(R.string.auth_email_placeholder),
                    keyboardType = KeyboardType.Email,
                )

                Spacer(modifier = Modifier.height(MoilAuthDimension.SectionSpacing))

                AuthFieldLabel(text = stringResource(R.string.auth_password))
                AuthTextField(
                    value = uiState.password,
                    onValueChange = { password ->
                        onEvent(DeleteAccountScreenEvent.PasswordChanged(password))
                    },
                    placeholder = stringResource(R.string.auth_password),
                    isPassword = true,
                )

                uiState.error?.let { error ->
                    AuthErrorText(text = error.toUserMessage(resources))
                }

                Spacer(modifier = Modifier.height(MoilAuthDimension.DescriptionBottomSpacing))

                DeleteAccountKeepSchedulesCard(
                    shouldKeepSchedules = uiState.shouldKeepSchedules,
                    isEnabled = isInputEnabled,
                    onKeepSchedulesChanged = { shouldKeepSchedules ->
                        onEvent(DeleteAccountScreenEvent.KeepSchedulesChanged(shouldKeepSchedules))
                    },
                )
            }
        }

        if (!uiState.isSocialAccount) {
            AccountSubmitButton(
                text = stringResource(R.string.account_delete_submit),
                enabled = uiState.canSubmit,
                isInProgress = uiState.isDeleting,
                containerColor = LocalMoilExtraColors.current.destructive,
                onClick = { onEvent(DeleteAccountScreenEvent.SubmitClicked) },
            )
        }
    }
}

@Composable
private fun DeleteAccountNotice(text: String) {
    Text(
        text = stringResource(R.string.account_delete_notice_bullet, text),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun DeleteAccountScreenContentPreview() {
    MoilTheme(darkTheme = true) {
        DeleteAccountScreenContent(
            uiState = DeleteAccountUiState(),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun DeleteAccountSocialScreenContentPreview() {
    MoilTheme(darkTheme = false) {
        DeleteAccountScreenContent(
            uiState = DeleteAccountUiState(isSocialAccount = true),
            onEvent = {},
        )
    }
}
