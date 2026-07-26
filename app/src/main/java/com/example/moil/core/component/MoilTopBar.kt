package com.example.moil.core.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moil.R
import com.example.moil.ui.theme.MoilComponentSize
import com.example.moil.ui.theme.MoilIconSize
import com.example.moil.ui.theme.MoilRadius
import com.example.moil.ui.theme.MoilTheme

@Composable
fun MoilTopBar(
    groupName: String,
    groupMenuContentDescription: String,
    @DrawableRes groupMemberAvatarResources: List<Int>,
    searchContentDescription: String,
    onGroupClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MoilGroupSelector(
            groupName = groupName,
            groupMenuContentDescription = groupMenuContentDescription,
            groupMemberAvatarResources = groupMemberAvatarResources,
            onClick = onGroupClick,
        )

        Spacer(modifier = Modifier.weight(1f))

        MoilSearchButton(
            contentDescription = searchContentDescription,
            onClick = onSearchClick,
        )
    }
}

@Composable
fun MoilGroupSelector(
    groupName: String,
    groupMenuContentDescription: String,
    @DrawableRes groupMemberAvatarResources: List<Int>,
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
            painter = painterResource(R.drawable.calendar_expand),
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
fun MoilSearchButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(MoilComponentSize.TopBarItem)
            .clip(CircleShape)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.common_search),
            contentDescription = contentDescription,
            modifier = Modifier.size(MoilIconSize.HeaderAction),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun MoilGroupAvatarStack(
    @DrawableRes groupMemberAvatarResources: List<Int>,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        groupMemberAvatarResources.forEachIndexed { avatarIndex, avatarResource ->
            Image(
                painter = painterResource(avatarResource),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            if (avatarIndex != groupMemberAvatarResources.lastIndex) {
                Spacer(modifier = Modifier.width((-4).dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoilTopBarPreview() {
    MoilTheme(darkTheme = false) {
        MoilTopBar(
            groupName = stringResource(R.string.calendar_family_name),
            groupMenuContentDescription = stringResource(R.string.calendar_group_menu),
            groupMemberAvatarResources = listOf(
                R.drawable.family_avatar_mine,
                R.drawable.family_avatar_mom,
                R.drawable.family_avatar_dad,
                R.drawable.family_avatar_sibling,
            ),
            searchContentDescription = stringResource(R.string.calendar_search),
            onGroupClick = {},
            onSearchClick = {},
        )
    }
}
