package com.example.moil.feature.profile.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.moil.ui.theme.LocalMoilExtraColors

@Composable
internal fun ProfileSectionLabel(text: String) {
    Text(
        text = text,
        color = LocalMoilExtraColors.current.scheduleMutedText,
        style = MaterialTheme.typography.labelMedium,
    )
}
