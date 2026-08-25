package com.example.moil.feature.family.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.moil.ui.theme.LocalMoilExtraColors

@Composable
internal fun FamilySectionTitle(text: String) {
    Text(
        text = text,
        color = LocalMoilExtraColors.current.scheduleMutedText,
        style = MaterialTheme.typography.labelMedium,
    )
}
