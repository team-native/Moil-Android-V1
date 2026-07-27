package com.example.moil.feature.family.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.moil.R
import com.example.moil.ui.theme.MoilMemberDimension
import com.example.moil.ui.theme.MoilTheme

@Composable
internal fun FamilyMemberRow(
    @StringRes nameRes: Int,
    @DrawableRes avatarRes: Int,
    @StringRes roleRes: Int,
    customName: String?,
    presenceColor: Color? = null,
    rowHeight: Dp = MoilMemberDimension.MemberRowHeight,
    avatarSize: Dp = MoilMemberDimension.MemberAvatarSize,
    horizontalPadding: Dp = MoilMemberDimension.ListItemHorizontalPadding,
    contentSpacing: Dp = MoilMemberDimension.ListItemContentSpacing,
    presenceIndicatorSize: Dp = MoilMemberDimension.GroupColorIndicatorSize,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .height(rowHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(avatarRes),
            contentDescription = customName ?: stringResource(nameRes),
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.width(contentSpacing))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = customName ?: stringResource(nameRes),
                style = MaterialTheme.typography.titleSmall,
            )

            Text(
                text = stringResource(roleRes),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        if (presenceColor != null) {
            Spacer(
                modifier = Modifier
                    .size(presenceIndicatorSize)
                    .clip(CircleShape)
                    .background(presenceColor),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FamilyMemberRowPreview() {
    MoilTheme(darkTheme = false) {
        FamilyMemberRow(
            nameRes = R.string.family_member_me,
            avatarRes = R.drawable.family_avatar_mine,
            roleRes = R.string.family_member_role,
            customName = null,
            presenceColor = MaterialTheme.colorScheme.secondary,
        )
    }
}
