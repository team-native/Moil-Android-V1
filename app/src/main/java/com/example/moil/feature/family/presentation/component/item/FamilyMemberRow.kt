package com.example.moil.feature.family.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.moil.R
import com.example.moil.core.model.GroupProfileColor
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilMemberDimension
import com.example.moil.ui.theme.MoilTheme

@Composable
internal fun FamilyMemberRow(
    name: String,
    @StringRes roleRes: Int,
    profileColor: GroupProfileColor,
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
        Box(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .background(memberAvatarColor(profileColor)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.take(1),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelLarge,
            )
        }

        Spacer(modifier = Modifier.width(contentSpacing))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
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
            name = "나",
            roleRes = R.string.family_member_role,
            profileColor = GroupProfileColor.Cyan,
            presenceColor = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun memberAvatarColor(profileColor: GroupProfileColor): Color = when (profileColor) {
    GroupProfileColor.Cyan -> LocalMoilExtraColors.current.memberCyan
    GroupProfileColor.Violet -> LocalMoilExtraColors.current.memberViolet
    GroupProfileColor.Rose -> LocalMoilExtraColors.current.memberRose
}
