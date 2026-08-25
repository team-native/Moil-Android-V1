package com.example.moil.feature.family.presentation

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
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.viewmodel.avatarResourceForGroupColor
import com.example.moil.ui.theme.LocalMoilExtraColors
import com.example.moil.ui.theme.MoilMemberDimension
import com.example.moil.ui.theme.MoilTheme

@Composable
internal fun FamilyMemberRow(
    name: String,
    @StringRes roleRes: Int,
    profileColor: GroupColor,
    rowHeight: Dp = MoilMemberDimension.MemberRowHeight,
    avatarSize: Dp = MoilMemberDimension.MemberAvatarSize,
    horizontalPadding: Dp = MoilMemberDimension.ListItemHorizontalPadding,
    contentSpacing: Dp = MoilMemberDimension.ListItemContentSpacing,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .height(rowHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(avatarResourceForGroupColor(profileColor)),
            contentDescription = null,
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )

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

        Spacer(
            modifier = Modifier
                .size(MoilMemberDimension.GroupColorIndicatorSize)
                .clip(CircleShape)
                .background(memberPresenceColor(profileColor)),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FamilyMemberRowPreview() {
    MoilTheme(darkTheme = false) {
        FamilyMemberRow(
            name = "나",
            roleRes = R.string.family_member_role,
            profileColor = GroupColor.Teal,
        )
    }
}

@Composable
private fun memberPresenceColor(profileColor: GroupColor): Color {
    val extraColors = LocalMoilExtraColors.current

    return when (profileColor) {
        GroupColor.Sky -> extraColors.profileSky
        GroupColor.Red -> extraColors.profileRed
        GroupColor.Green -> extraColors.profileGreen
        GroupColor.Yellow -> extraColors.profileYellow
        GroupColor.Teal -> extraColors.profileTeal
        GroupColor.Violet -> extraColors.profileViolet
        GroupColor.Magenta -> extraColors.profileMagenta
        GroupColor.Unknown -> extraColors.calendarMutedText
    }
}
