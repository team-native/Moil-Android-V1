package com.example.moil.feature.group.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.MoilTextField
import com.example.moil.ui.theme.MoilGroupCreateDimension
import com.example.moil.ui.theme.MoilSpacing

@Composable
internal fun JoinGroupInviteCodeField(
    inviteCode: String,
    onInviteCodeChanged: (String) -> Unit,
) {
    Column(modifier = Modifier.padding(top = MoilSpacing.ContentTop)) {
        Text(
            text = stringResource(R.string.group_join_invite_code_label),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )

        MoilTextField(
            value = inviteCode,
            onValueChange = onInviteCodeChanged,
            placeholder = stringResource(R.string.group_join_invite_code_placeholder),
            modifier = Modifier.padding(top = MoilGroupCreateDimension.HeaderTitleSpacing),
        )
    }
}
