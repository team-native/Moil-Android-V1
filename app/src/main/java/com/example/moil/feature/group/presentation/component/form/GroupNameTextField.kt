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
internal fun GroupNameTextField(
    value: String,
    groupNameError: CreateGroupNameError?,
    onValueChange: (String) -> Unit,
) {
    Column {
        Text(
            text = stringResource(R.string.group_name_label),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )

        MoilTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = stringResource(R.string.group_name_placeholder),
            modifier = Modifier.padding(top = MoilGroupCreateDimension.HeaderTitleSpacing),
        )

        if (groupNameError != null) {
            Text(
                text = stringResource(
                    when (groupNameError) {
                        CreateGroupNameError.Duplicate -> R.string.group_name_duplicate_error
                        CreateGroupNameError.MissingUserName -> R.string.group_name_missing_user_name_error
                    },
                ),
                modifier = Modifier.padding(top = MoilGroupCreateDimension.HeaderTitleSpacing),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
