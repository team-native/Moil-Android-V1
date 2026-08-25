package com.example.moil.feature.family.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.moil.feature.family.viewmodel.GroupUiModel
import com.example.moil.ui.theme.MoilMemberDimension

@Composable
internal fun FamilyGroupTabs(
    groups: List<GroupUiModel>,
    selectedGroupId: String,
    onGroupClick: (String) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MoilMemberDimension.GroupTabSpacing),
    ) {
        items(items = groups, key = { group -> group.id }) { group ->
            val isSelected = group.id == selectedGroupId

            Surface(
                modifier = Modifier
                    .defaultMinSize(minWidth = MoilMemberDimension.GroupTabMinWidth)
                    .height(MoilMemberDimension.GroupTabHeight)
                    .clickable { onGroupClick(group.id) },
                shape = RoundedCornerShape(percent = 50),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = MoilMemberDimension.GroupTabHorizontalPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = group.name,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}
