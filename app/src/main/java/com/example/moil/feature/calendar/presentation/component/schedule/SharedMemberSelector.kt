package com.example.moil.feature.calendar.presentation.component.schedule

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.example.moil.feature.calendar.presentation.CalendarMemberId
import com.example.moil.ui.theme.LocalMoilExtraTypography
import com.example.moil.ui.theme.MoilScheduleSheet

@Composable
internal fun SharedMemberSelector(
    sharedMemberIds: Set<CalendarMemberId>,
    onMemberClick: (CalendarMemberId) -> Unit,
) {
    val selectedMemberRingColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
    val selectedMemberRingGapColor = androidx.compose.material3.MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MoilScheduleSheet.HorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(MoilScheduleSheet.SharedMemberSpacing),
    ) {
        scheduleCalendarMembers.forEach { calendarMember ->
            val isSelected = calendarMember.id in sharedMemberIds
            val memberName = stringResource(calendarMember.nameRes)
            Column(
                modifier = Modifier
                    .width(MoilScheduleSheet.SharedMemberAvatarSize)
                    .clickable(role = Role.Checkbox) {
                        onMemberClick(calendarMember.id)
                    }
                    .semantics {
                        contentDescription = memberName
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(MoilScheduleSheet.SharedMemberAvatarSize)
                        .drawBehind {
                            if (isSelected) {
                                val avatarRadius = size.minDimension / 2
                                val primaryRingRadius = avatarRadius +
                                    MoilScheduleSheet.SelectedMemberWhiteRing.toPx() +
                                    MoilScheduleSheet.SelectedMemberPrimaryRing.toPx()
                                val whiteRingRadius = avatarRadius +
                                    MoilScheduleSheet.SelectedMemberWhiteRing.toPx()

                                drawCircle(
                                    color = selectedMemberRingColor,
                                    radius = primaryRingRadius,
                                )
                                drawCircle(
                                    color = selectedMemberRingGapColor,
                                    radius = whiteRingRadius,
                                )
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(calendarMember.avatarRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(MoilScheduleSheet.SharedMemberAvatarSize)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                }

                Spacer(modifier = Modifier.height(MoilScheduleSheet.SharedMemberLabelGap))

                androidx.compose.material3.Text(
                    text = memberName,
                    style = LocalMoilExtraTypography.current.scheduleMemberName.copy(
                        fontWeight = if (isSelected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                    ),
                )
            }
        }
    }
}
