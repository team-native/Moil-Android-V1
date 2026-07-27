package com.example.moil.feature.group.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.MoilPrimaryButton
import com.example.moil.ui.theme.MoilGroupCreateDimension

@Composable
internal fun CreateGroupActionButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    MoilPrimaryButton(
        text = stringResource(R.string.group_create_action),
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = MoilGroupCreateDimension.BottomButtonPadding,
            )
            .height(MoilGroupCreateDimension.BottomButtonHeight),
    )
}
