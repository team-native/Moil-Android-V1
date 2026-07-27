package com.example.moil.feature.calendar.presentation.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.moil.R

@Composable
internal fun ScheduleLocationDialog(
    initialLocation: String,
    onLocationConfirmed: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var locationInput by remember(initialLocation) {
        mutableStateOf(initialLocation)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onLocationConfirmed(locationInput)
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
        title = {
            Text(text = stringResource(R.string.schedule_location_dialog_title))
        },
        text = {
            OutlinedTextField(
                value = locationInput,
                onValueChange = { locationInput = it },
                placeholder = {
                    Text(text = stringResource(R.string.schedule_location_placeholder))
                },
                singleLine = true,
            )
        },
    )
}
