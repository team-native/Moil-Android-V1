package com.example.moil.feature.calendar.view

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilScheduleSheet

@Composable
internal fun ScheduleInfoRow(
    @StringRes labelRes: Int,
    value: String,
    onClick: (() -> Unit)? = null,
    valueColor: Color = MaterialTheme.colorScheme.outline,
    showDivider: Boolean = true,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    if (showDivider) {
                        MoilScheduleSheet.FormRowHeight - 1.dp
                    } else {
                        MoilScheduleSheet.FormRowHeight
                    },
                )
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            role = Role.Button,
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = MoilScheduleSheet.HorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(labelRes),
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = value,
                color = valueColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if (showDivider) {
            HorizontalDivider(color = LocalMoilExtraColors.current.scheduleDivider)
        }
    }
}
