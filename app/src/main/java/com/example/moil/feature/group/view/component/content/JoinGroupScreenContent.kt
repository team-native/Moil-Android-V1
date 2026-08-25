package com.example.moil.feature.group.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.moil.R
import com.example.moil.core.component.MoilNavigationDestination
import com.example.moil.core.component.MoilPrimaryButton
import com.example.moil.core.component.MoilTabScaffold
import com.example.moil.feature.group.viewmodel.JoinGroupScreenEvent
import com.example.moil.feature.group.viewmodel.JoinGroupUiState
import com.example.moil.ui.theme.MoilGroupCreateDimension
import com.example.moil.ui.theme.MoilGroupJoinDimension
import com.example.moil.ui.theme.LocalMoilExtraTypography
import com.example.moil.ui.theme.MoilSpacing
import com.example.moil.ui.theme.MoilTheme

@Composable
internal fun JoinGroupScreenContent(
    uiState: JoinGroupUiState,
    onEvent: (JoinGroupScreenEvent) -> Unit,
) {
    MoilTabScaffold(
        selectedDestination = MoilNavigationDestination.JoinGroup,
        onDestinationClick = { destination ->
            onEvent(JoinGroupScreenEvent.DestinationClicked(destination))
        },
    ) { contentModifier ->
        Column(
            modifier = contentModifier,
        ) {
            Text(
                text = stringResource(R.string.group_join_title),
                style = LocalMoilExtraTypography.current.groupJoinTitle,
            )

            Text(
                text = stringResource(R.string.group_join_description),
                modifier = Modifier.padding(top = MoilSpacing.CalendarRow),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )

            JoinGroupInviteCodeField(
                inviteCode = uiState.inviteCode,
                onInviteCodeChanged = { inviteCode ->
                    onEvent(JoinGroupScreenEvent.InviteCodeChanged(inviteCode))
                },
            )

            Spacer(modifier = Modifier.weight(1f))

            MoilPrimaryButton(
                text = stringResource(R.string.group_join_action),
                onClick = { onEvent(JoinGroupScreenEvent.InviteCodeConfirmed) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MoilGroupCreateDimension.BottomButtonHeight),
            )

            Spacer(modifier = Modifier.height(MoilGroupJoinDimension.BottomButtonSpacing))
        }
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun JoinGroupScreenContentPreview() {
    MoilTheme(darkTheme = false) {
        JoinGroupScreenContent(
            uiState = JoinGroupUiState(),
            onEvent = {},
        )
    }
}
