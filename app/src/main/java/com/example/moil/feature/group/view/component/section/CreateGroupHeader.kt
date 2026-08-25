package com.example.moil.feature.group.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.example.moil.R
import com.example.moil.core.component.MoilImageButton
import com.example.moil.ui.theme.MoilGroupCreateDimension

@Composable
internal fun CreateGroupHeader(onBackClick: () -> Unit) {
    GroupPageHeader(
        titleRes = R.string.group_create_title,
        onBackClick = onBackClick,
    )
}

@Composable
internal fun GroupPageHeader(
    @androidx.annotation.StringRes titleRes: Int,
    onBackClick: () -> Unit,
    titleStyle: TextStyle? = null,
) {
    val backContentDescription = stringResource(R.string.group_create_back)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MoilGroupCreateDimension.HeaderTopPadding)
            .height(MoilGroupCreateDimension.HeaderHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MoilImageButton(
            imageRes = R.drawable.common_chevron_back,
            contentDescription = backContentDescription,
            onClick = onBackClick,
            modifier = Modifier
                .size(MoilGroupCreateDimension.HeaderIconTouchTarget),
            imageModifier = Modifier.size(
                width = MoilGroupCreateDimension.HeaderIconWidth,
                height = MoilGroupCreateDimension.HeaderIconHeight,
            ),
        )

        Text(
            text = stringResource(titleRes),
            modifier = Modifier.padding(start = MoilGroupCreateDimension.HeaderTitleSpacing),
            style = titleStyle ?: MaterialTheme.typography.titleMedium,
        )
    }
}
