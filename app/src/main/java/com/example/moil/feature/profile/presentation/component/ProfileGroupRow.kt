package com.example.moil.feature.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.moil.R
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.LocalMoilIsDarkTheme
import com.example.moil.ui.theme.MoilProfileDimension

@Composable
internal fun ProfileGroupRow(
    group: ProfileGroupUiModel,
    showDivider: Boolean,
    onClick: () -> Unit,
) {
    val groupChevronDrawableRes = if (LocalMoilIsDarkTheme.current) {
        R.drawable.common_chevron_next_dark
    } else {
        R.drawable.common_chevron_next
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilProfileDimension.GroupRowHeight)
                .clickable(onClick = onClick)
                .padding(horizontal = MoilProfileDimension.GroupRowHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(
                modifier = Modifier
                    .size(MoilProfileDimension.GroupIndicatorSize)
                    .clip(CircleShape)
                    .background(profileGroupIndicatorColor(group.indicator)),
            )

            Spacer(modifier = Modifier.width(MoilProfileDimension.GroupRowContentSpacing))

            Text(
                text = group.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall,
            )

            Image(
                painter = painterResource(groupChevronDrawableRes),
                contentDescription = null,
                modifier = Modifier.size(
                    width = MoilProfileDimension.ChevronWidth,
                    height = MoilProfileDimension.ChevronHeight,
                ),
                contentScale = ContentScale.Fit,
            )
        }

        if (showDivider) {
            HorizontalDivider(color = LocalMoilExtraColors.current.scheduleDivider)
        }
    }
}

@Composable
private fun profileGroupIndicatorColor(indicator: ProfileGroupIndicator) = when (indicator) {
    ProfileGroupIndicator.Primary -> LocalMoilExtraColors.current.profileGroupPrimaryIndicator
    ProfileGroupIndicator.Secondary -> LocalMoilExtraColors.current.profileGroupSecondaryIndicator
}
