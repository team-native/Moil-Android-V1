package com.example.moil.feature.calendar.view

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.core.component.MoilSwitch
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilScheduleSheet

@Composable
internal fun ScheduleAllDayRow(
    isAllDay: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MoilScheduleSheet.AllDayRowHeight - 1.dp)
                .padding(horizontal = MoilScheduleSheet.HorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.schedule_all_day),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
            )

            MoilSwitch(
                checked = isAllDay,
                onCheckedChange = onCheckedChange,
            )
        }

        HorizontalDivider(color = LocalMoilExtraColors.current.scheduleDivider)
    }
}
