package com.example.moil.feature.calendar.presentation.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.ApplyDialogWindowBackgroundBlur
import com.example.moil.ui.theme.MoilTimePickerDimension
import java.time.LocalTime

@Composable
internal fun ScheduleTimePickerDialog(
    selectedTime: LocalTime,
    onTimeConfirmed: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedHour by remember(selectedTime) {
        mutableIntStateOf(selectedTime.toTwelveHour())
    }
    var selectedMinute by remember(selectedTime) {
        mutableIntStateOf(selectedTime.minute)
    }
    var isPm by remember(selectedTime) {
        mutableStateOf(selectedTime.hour >= NOON_HOUR)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onTimeConfirmed(
                        LocalTime.of(
                            selectedHour.toTwentyFourHour(isPm),
                            selectedMinute,
                        ),
                    )
                },
            ) {
                Text(text = stringResource(R.string.schedule_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.schedule_cancel))
            }
        },
        text = {
            ApplyDialogWindowBackgroundBlur(
                blurRadius = MoilTimePickerDimension.BackgroundBlur,
            )

            ScheduleTimeSelector(
                hour = selectedHour,
                minute = selectedMinute,
                isPm = isPm,
                onHourScroll = { selectedHour = selectedHour.cycleHour(it) },
                onMinuteScroll = { selectedMinute = selectedMinute.cycleMinute(it) },
                onMeridiemSelected = { selectedIsPm -> isPm = selectedIsPm },
            )
        },
    )
}

@Composable
private fun ScheduleTimeSelector(
    hour: Int,
    minute: Int,
    isPm: Boolean,
    onHourScroll: (Int) -> Unit,
    onMinuteScroll: (Int) -> Unit,
    onMeridiemSelected: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ScrollableTimeValue(
            value = hour.toString(),
            contentDescription = stringResource(R.string.schedule_time_hour_picker),
            onScrollStep = onHourScroll,
        )

        TimeSeparator()

        ScrollableTimeValue(
            value = minute.toString().padStart(MINUTE_DIGIT_COUNT, '0'),
            contentDescription = stringResource(R.string.schedule_time_minute_picker),
            onScrollStep = onMinuteScroll,
        )

        MeridiemSelector(
            isPm = isPm,
            onMeridiemSelected = onMeridiemSelected,
        )
    }
}

@Composable
private fun TimeSeparator() {
    Box(
        modifier = Modifier
            .width(MoilTimePickerDimension.SeparatorWidth)
            .height(MoilTimePickerDimension.TimeValueHeight),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = ":",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ScrollableTimeValue(
    value: String,
    contentDescription: String,
    onScrollStep: (Int) -> Unit,
) {
    var scrollRemainder by remember { mutableStateOf(0f) }
    val scrollThreshold = with(LocalDensity.current) {
        MoilTimePickerDimension.ScrollStep.toPx()
    }
    val scrollState = rememberScrollableState { scrollDelta ->
        scrollRemainder += scrollDelta

        while (scrollRemainder >= scrollThreshold) {
            onScrollStep(SCROLL_UP_STEP)
            scrollRemainder -= scrollThreshold
        }

        while (scrollRemainder <= -scrollThreshold) {
            onScrollStep(SCROLL_DOWN_STEP)
            scrollRemainder += scrollThreshold
        }

        scrollDelta
    }

    Box(
        modifier = Modifier
            .width(MoilTimePickerDimension.TimeValueWidth)
            .height(MoilTimePickerDimension.TimeValueHeight)
            .scrollable(
                state = scrollState,
                orientation = Orientation.Vertical,
            )
            .semantics {
                this.contentDescription = contentDescription
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun MeridiemSelector(
    isPm: Boolean,
    onMeridiemSelected: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(MoilTimePickerDimension.MeridiemWidth)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onMeridiemSelected(false) },
        ) {
            Text(
                text = stringResource(R.string.schedule_time_am_short),
                color = if (isPm) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.primary
                },
            )
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onMeridiemSelected(true) },
        ) {
            Text(
                text = stringResource(R.string.schedule_time_pm_short),
                color = if (isPm) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}

private fun LocalTime.toTwelveHour(): Int = when (hour) {
    MIDNIGHT_HOUR,
    NOON_HOUR,
    -> TWELVE_HOUR_CLOCK_MAX

    else -> hour % NOON_HOUR
}

private fun Int.toTwentyFourHour(isPm: Boolean): Int = when {
    this == TWELVE_HOUR_CLOCK_MAX && !isPm -> MIDNIGHT_HOUR
    this == TWELVE_HOUR_CLOCK_MAX && isPm -> NOON_HOUR
    isPm -> this + NOON_HOUR
    else -> this
}

private fun Int.cycleHour(step: Int): Int {
    val adjustedHour = this + step

    return when {
        adjustedHour > TWELVE_HOUR_CLOCK_MAX -> FIRST_HOUR
        adjustedHour < FIRST_HOUR -> TWELVE_HOUR_CLOCK_MAX
        else -> adjustedHour
    }
}

private fun Int.cycleMinute(step: Int): Int = (this + step).mod(MINUTES_PER_HOUR)

private const val MIDNIGHT_HOUR = 0
private const val FIRST_HOUR = 1
private const val NOON_HOUR = 12
private const val TWELVE_HOUR_CLOCK_MAX = 12
private const val MINUTES_PER_HOUR = 60
private const val MINUTE_DIGIT_COUNT = 2
private const val SCROLL_UP_STEP = 1
private const val SCROLL_DOWN_STEP = -1
