package com.example.moil.feature.profile.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.feature.profile.viewmodel.ProfileGroupUiModel
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilProfileDimension

@Composable
internal fun ProfileGroupCard(
    groups: List<ProfileGroupUiModel>,
    onGroupClick: (String) -> Unit,
    onCreateGroupClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilProfileDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Column {
            groups.forEachIndexed { index, group ->
                ProfileGroupRow(
                    group = group,
                    showDivider = index < groups.lastIndex,
                    onClick = { onGroupClick(group.id) },
                )
            }

            if (groups.isNotEmpty()) {
                HorizontalDivider(color = LocalMoilExtraColors.current.scheduleDivider)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MoilProfileDimension.GroupRowHeight)
                    .clickable(onClick = onCreateGroupClick)
                    .padding(horizontal = MoilProfileDimension.GroupRowHorizontalPadding),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = stringResource(R.string.profile_create_group),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}
