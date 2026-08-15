package com.example.moil.feature.calendar.presentation

import com.example.moil.feature.event.domain.GroupEvent
import com.example.moil.feature.group.domain.GroupColor
import com.example.moil.feature.group.domain.GroupMember
import com.example.moil.feature.group.domain.GroupSummary
import com.example.moil.feature.group.presentation.avatarResourceForGroupColor
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
        color = member.color,
        isCurrentUser = member.isMe == true,
        avatarRes = member.color.toAvatarResource(),
    )
}

internal fun List<GroupEvent>.toCalendarEventsByDate(
    fallbackProfileColor: GroupColor,
): Map<LocalDate, List<CalendarEventUiModel>> =
    mapNotNull { event ->
        runCatching { LocalDate.parse(event.date) }
            .getOrNull()
            ?.let { eventDate ->
                eventDate to event.toCalendarEventUiModel(fallbackProfileColor)
            }
    }.groupBy(
        keySelector = { (eventDate, _) -> eventDate },
        valueTransform = { (_, event) -> event },
    ).mapValues { (_, events) ->
        events.mapIndexed { lineIndex, calendarEvent ->
            calendarEvent.copy(lineIndex = lineIndex)
        }
    }

private fun GroupEvent.toCalendarEventUiModel(
    fallbackProfileColor: GroupColor,
): CalendarEventUiModel = CalendarEventUiModel(
    title = title,
    // A schedule uses one color so its calendar box stays visually distinct.
    displayColor = members.firstOrNull()?.color ?: fallbackProfileColor,
    lineIndex = 0,
)

private fun GroupColor.toAvatarResource(): Int = avatarResourceForGroupColor(this)
