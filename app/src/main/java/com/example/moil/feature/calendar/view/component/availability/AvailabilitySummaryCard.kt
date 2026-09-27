package com.example.moil.feature.calendar.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.feature.calendar.viewmodel.EventAvailabilityDisplayModel
import com.example.moil.feature.calendar.viewmodel.toAvailabilityServerTime
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilAvailabilityDimension

/**
 * "함께 보기" 탭 상단 요약이다. 응답 인원과 모두 되는 시간을 먼저 보여주고,
 * 모두 되는 시간이 없으면 가장 많이 되는 시간을 대신 알려준다.
 */
@Composable
internal fun AvailabilitySummaryCard(
    displayModel: EventAvailabilityDisplayModel,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MoilAvailabilityDimension.SummaryCornerRadius),
        color = LocalMoilExtraColors.current.overlaySurface,
    ) {
        Column(modifier = Modifier.padding(MoilAvailabilityDimension.SummaryPadding)) {
            Text(
                text = stringResource(
                    R.string.availability_responded,
                    displayModel.participantCount,
                    displayModel.respondedCount,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )

            Text(
                text = stringResource(R.string.availability_everyone_title),
                modifier = Modifier.padding(top = MoilAvailabilityDimension.CellGap),
                style = MaterialTheme.typography.titleSmall,
            )

            if (displayModel.everyoneRanges.isEmpty()) {
                Text(
                    text = stringResource(R.string.availability_everyone_none),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )

                displayModel.bestRange?.let { bestRange ->
                    Text(
                        text = stringResource(
                            R.string.availability_best_time,
                            bestRange.startTime.toAvailabilityServerTime(),
                            bestRange.endTime.toAvailabilityServerTime(),
                            bestRange.availableCount,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                displayModel.everyoneRanges.forEach { everyoneRange ->
                    Text(
                        text = stringResource(
                            R.string.availability_range,
                            everyoneRange.startTime.toAvailabilityServerTime(),
                            everyoneRange.endTime.toAvailabilityServerTime(),
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
