package com.example.moil.feature.profile.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.MoilImageButton
import com.example.moil.ui.theme.MoilProfileEditDimension

@Composable
internal fun ProfileEditHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MoilProfileEditDimension.HeaderTopPadding)
            .height(MoilProfileEditDimension.HeaderHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MoilImageButton(
            imageRes = R.drawable.common_chevron_back,
            contentDescription = stringResource(R.string.profile_edit_back),
            onClick = onBackClick,
            modifier = Modifier.size(MoilProfileEditDimension.HeaderIconTouchTarget),
            imageModifier = Modifier.size(
                width = MoilProfileEditDimension.HeaderIconWidth,
                height = MoilProfileEditDimension.HeaderIconHeight,
            ),
        )

        Text(
            text = stringResource(R.string.profile_edit_title),
            modifier = Modifier.padding(start = MoilProfileEditDimension.HeaderTitleSpacing),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
