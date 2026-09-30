package com.example.moil.navigation.route

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.network.InviteLinkFormatter
import com.example.moil.feature.family.view.FamilyInviteShareBottomSheet
import com.example.moil.navigation.MoilMainNavigator
import com.example.moil.navigation.MoilMainUiState

/** 그룹 초대 링크를 공유하거나 복사하는 바텀시트 목적지다. */
@Composable
internal fun InviteShareRoute(
    mainUiState: MoilMainUiState,
    navigator: MoilMainNavigator,
) {
    val context = LocalContext.current
    val inviteShareTitle = stringResource(R.string.family_invite_share_title)
    val selectedGroup = mainUiState.familyUiState.selectedGroup ?: return
    val inviteLink = InviteLinkFormatter.create(selectedGroup.id.toLong())

    FamilyInviteShareBottomSheet(
        inviteLink = inviteLink,
        onShareClick = {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = PLAIN_TEXT_MIME_TYPE
                putExtra(Intent.EXTRA_TEXT, inviteLink)
            }

            context.startActivity(Intent.createChooser(shareIntent, inviteShareTitle))
        },
        onCopyLinkClick = {
            val clipboardManager = context
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            clipboardManager.setPrimaryClip(
                ClipData.newPlainText(inviteShareTitle, inviteLink),
            )
            navigator.goBack()
        },
    )
}

private const val PLAIN_TEXT_MIME_TYPE = "text/plain"
