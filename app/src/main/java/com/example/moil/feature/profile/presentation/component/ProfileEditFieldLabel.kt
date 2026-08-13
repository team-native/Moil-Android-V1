package com.example.moil.feature.profile.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.moil.R

@Composable
internal fun ProfileEditFieldLabel() {
    Text(
        text = stringResource(R.string.profile_edit_name_label),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelMedium,
    )
}
