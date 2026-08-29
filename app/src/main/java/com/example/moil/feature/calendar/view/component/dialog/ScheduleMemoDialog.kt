package com.example.moil.feature.calendar.view

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.moil.R

@Composable
internal fun ScheduleMemoDialog(
    initialMemo: String,
    onMemoConfirmed: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var memoInput by remember(initialMemo) {
        mutableStateOf(initialMemo)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onMemoConfirmed(memoInput)
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
        title = {
            Text(text = stringResource(R.string.schedule_memo_dialog_title))
        },
        text = {
            OutlinedTextField(
                value = memoInput,
                onValueChange = { memoInput = it },
                placeholder = {
                    Text(text = stringResource(R.string.schedule_memo_placeholder))
                },
                minLines = 3,
                maxLines = 5,
            )
        },
    )
}
