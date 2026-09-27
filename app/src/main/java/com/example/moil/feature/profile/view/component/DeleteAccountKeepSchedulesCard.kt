package com.example.moil.feature.profile.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.MoilSwitch
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilProfileDimension

/** 탈퇴 후에도 내가 캘린더에 적은 정보를 남길지(API `leftData`) 고르는 카드다. */
@Composable
internal fun DeleteAccountKeepSchedulesCard(
    shouldKeepSchedules: Boolean,
    isEnabled: Boolean,
    onKeepSchedulesChanged: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilProfileDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MoilProfileDimension.GroupRowHorizontalPadding,
                    vertical = MoilProfileDimension.GroupRowContentSpacing,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.account_delete_keep_schedules),
                    style = MaterialTheme.typography.bodyMedium,
                )

                Text(
                    text = stringResource(R.string.account_delete_keep_schedules_description),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.width(MoilProfileDimension.GroupRowContentSpacing))

            MoilSwitch(
                checked = shouldKeepSchedules,
                onCheckedChange = onKeepSchedulesChanged,
                enabled = isEnabled,
            )
        }
    }
}
