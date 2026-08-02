package com.example.moil.core.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.ui.theme.MoilComponentSize
import com.example.moil.ui.theme.MoilRadius

@Composable
fun MoilGroupSelector(
    groupName: String,
    groupMenuContentDescription: String,
    @DrawableRes groupMemberAvatarResources: List<Int>,
    isGroupIndicatorExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(MoilComponentSize.TopBarItem)
            .clip(RoundedCornerShape(MoilRadius.Event))
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                contentDescription = groupMenuContentDescription
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MoilGroupAvatarStack(groupMemberAvatarResources)

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = groupName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.width(4.dp))

        Image(
            painter = painterResource(
                if (isGroupIndicatorExpanded) {
                    R.drawable.group_collapse_indicator
                } else {
                    R.drawable.group_expand_indicator
                },
            ),
            contentDescription = null,
            modifier = Modifier.size(
                width = 9.dp,
                height = 6.dp,
            ),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
        )
    }
}
