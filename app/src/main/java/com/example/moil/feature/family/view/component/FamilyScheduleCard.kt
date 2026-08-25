package com.example.moil.feature.family.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.moil.R
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilGroupDetailDimension
import java.time.YearMonth

@Composable
internal fun FamilyScheduleCard(
    month: YearMonth,
    eventCount: Int,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilGroupDetailDimension.CardCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Text(
            text = stringResource(R.string.family_month_schedule_count, month.monthValue, eventCount),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MoilGroupDetailDimension.ScheduleCardVerticalPadding),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}
