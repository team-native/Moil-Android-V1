package com.example.moil.feature.calendar.presentation.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ScheduleTimePickerDialog(
    selectedTime: LocalTime,
    onTimeConfirmed: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime.hour,
        initialMinute = selectedTime.minute,
        is24Hour = false,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onTimeConfirmed(
                        LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute,
                        ),
                    )
                },
            ) {
                Text(text = stringResource(R.string.schedule_save))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.schedule_cancel))
            }
        },
        text = {
            TimePicker(state = timePickerState)
        },
    )
}
