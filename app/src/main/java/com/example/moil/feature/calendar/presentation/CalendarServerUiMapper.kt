package com.example.moil.feature.calendar.presentation

import com.example.moil.R
import com.example.moil.feature.event.domain.GroupEvent
import com.example.moil.feature.group.domain.GroupColor
import com.example.moil.feature.group.domain.GroupMember
import com.example.moil.feature.group.domain.GroupSummary
import java.time.LocalDate

internal fun List<GroupSummary>.toCalendarGroups(): List<CalendarGroupUiModel> = map { group ->
    CalendarGroupUiModel(
        id = group.id,
        name = group.name,
    )
}

internal fun List<GroupMember>.toCalendarMembers(): List<CalendarMemberUiModel> = map { member ->
    CalendarMemberUiModel(
        id = member.userId,
        name = member.nickname,
        avatarRes = member.color.toAvatarResource(),
    )
}

internal fun List<GroupEvent>.toCalendarEventsByDate(): Map<LocalDate, List<CalendarEventUiModel>> =
    mapNotNull { event ->
        runCatching { LocalDate.parse(event.date) }
            .getOrNull()
            ?.let { eventDate -> eventDate to event.toCalendarEventUiModel() }
    }.groupBy(
        keySelector = { (eventDate, _) -> eventDate },
        valueTransform = { (_, event) -> event },
    )

private fun GroupEvent.toCalendarEventUiModel(): CalendarEventUiModel = CalendarEventUiModel(
    title = title,
    color = members.firstOrNull()?.color.toCalendarEventUiColor(),
)

private fun GroupColor?.toCalendarEventUiColor(): CalendarEventUiColor = when (this) {
    GroupColor.Green,
    GroupColor.Teal -> CalendarEventUiColor.Green
    GroupColor.Yellow -> CalendarEventUiColor.Yellow
    else -> CalendarEventUiColor.Blue
}

private fun GroupColor.toAvatarResource(): Int = when (this) {
    GroupColor.Sky -> R.drawable.family_avatar_mom
    GroupColor.Red -> R.drawable.family_avatar_mine
    GroupColor.Green -> R.drawable.family_avatar_member_teal
    GroupColor.Yellow -> R.drawable.family_avatar_member_green
    GroupColor.Teal -> R.drawable.family_avatar_member_blue
    GroupColor.Violet -> R.drawable.family_avatar_dad
    GroupColor.Magenta -> R.drawable.family_avatar_sibling
    GroupColor.Unknown -> R.drawable.family_avatar_mine
}
