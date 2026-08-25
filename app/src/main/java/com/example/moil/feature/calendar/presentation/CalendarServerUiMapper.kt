package com.example.moil.feature.calendar.presentation

import com.example.moil.feature.event.module.domain.model.GroupEvent
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

internal fun List<GroupEvent>.toCalendarEvents(
    fallbackProfileColor: GroupColor,
): List<CalendarEventUiModel> =
    mapNotNull { event ->
        runCatching { LocalDate.parse(event.startDate) to LocalDate.parse(event.endDate) }
            .getOrNull()
            ?.takeIf { (startDate, endDate) -> !endDate.isBefore(startDate) }
            ?.let { (startDate, endDate) ->
                event.toCalendarEventUiModel(
                    startDate = startDate,
                    endDate = endDate,
                    fallbackProfileColor = fallbackProfileColor,
                )
            }
    }.assignCalendarEventLines()

private fun GroupEvent.toCalendarEventUiModel(
    startDate: LocalDate,
    endDate: LocalDate,
    fallbackProfileColor: GroupColor,
): CalendarEventUiModel = CalendarEventUiModel(
    id = id,
    title = title,
    startDate = startDate,
    endDate = endDate,
    // A schedule uses one color so its calendar box stays visually distinct.
    displayColor = members.firstOrNull()?.color ?: fallbackProfileColor,
    lineIndex = 0,
)

private fun List<CalendarEventUiModel>.assignCalendarEventLines(): List<CalendarEventUiModel> {
    val laneEndDates = mutableListOf<LocalDate>()

    return sortedWith(compareBy<CalendarEventUiModel> { it.startDate }.thenBy { it.endDate }.thenBy { it.id })
        .map { event ->
            val availableLaneIndex = laneEndDates.indexOfFirst { laneEndDate ->
                laneEndDate.isBefore(event.startDate)
            }
            val laneIndex = if (availableLaneIndex >= 0) {
                availableLaneIndex
            } else {
                laneEndDates.size
            }

            if (laneIndex == laneEndDates.size) {
                laneEndDates += event.endDate
            } else {
                laneEndDates[laneIndex] = event.endDate
            }

            event.copy(lineIndex = laneIndex)
        }
}

private fun GroupColor.toAvatarResource(): Int = avatarResourceForGroupColor(this)
