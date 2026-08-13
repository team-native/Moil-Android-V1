package com.example.moil.feature.group.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.moil.R
import com.example.moil.ui.theme.MoilMemberDimension

@Composable
internal fun JoinedGroupSummary(
    groupName: String,
    memberCount: Int,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        Row(
            modifier = Modifier.padding(MoilMemberDimension.ListItemHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(text = groupName, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = stringResource(R.string.group_member_count_format, memberCount),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
