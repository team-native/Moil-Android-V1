package com.example.moil.feature.calendar.view

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.moil.R

@Composable
internal fun ScheduleDeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.schedule_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.schedule_cancel))
            }
        },
        title = {
            Text(text = stringResource(R.string.schedule_delete_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.schedule_delete_dialog_message))
        },
    )
}
