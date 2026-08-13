package com.example.moil.feature.group.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.core.component.MoilTextField
import com.example.moil.ui.theme.MoilGroupCreateDimension

@Composable
internal fun ProfileNameField(
    profileName: String,
    onProfileNameChanged: (String) -> Unit,
) {
    Column {
        Text(
            text = stringResource(R.string.group_join_profile_name_label),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )

        MoilTextField(
            value = profileName,
            onValueChange = onProfileNameChanged,
            placeholder = stringResource(R.string.group_join_profile_name_placeholder),
            modifier = Modifier.padding(top = MoilGroupCreateDimension.HeaderTitleSpacing),
        )
    }
}
