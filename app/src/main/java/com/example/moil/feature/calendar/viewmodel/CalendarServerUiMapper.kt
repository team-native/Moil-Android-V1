package com.example.moil.feature.calendar.viewmodel

import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.GroupMember
import com.example.moil.feature.group.module.domain.model.GroupSummary
import com.example.moil.feature.group.viewmodel.avatarResourceForGroupColor
import java.time.LocalDate
import java.time.LocalTime

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
        imagePath = member.imagePath,
        avatarRes = member.color.toAvatarResource(),
    )
}

internal fun List<GroupEvent>.toCalendarEvents(
    fallbackProfileColor: GroupColor,
): List<CalendarEventUiModel> =
    mapNotNull { event ->
        runCatching { LocalDate.parse(event.date) }
            .getOrNull()
            ?.let { eventDate ->
                event.toCalendarEventUiModel(
                    eventDate = eventDate,
                    fallbackProfileColor = fallbackProfileColor,
                )
            }
    }.assignCalendarEventLines()

internal fun List<GroupEvent>.toCalendarSchedules(
    fallbackProfileColor: GroupColor,
    groupMembers: List<GroupMember> = emptyList(),
): List<CalendarScheduleUiModel> = mapNotNull { event ->
    runCatching { event.toCalendarScheduleUiModel(fallbackProfileColor, groupMembers) }.getOrNull()
}

internal fun GroupEvent.toCalendarScheduleUiModel(
    fallbackProfileColor: GroupColor,
    groupMembers: List<GroupMember> = emptyList(),
): CalendarScheduleUiModel = CalendarScheduleUiModel(
    id = id,
    title = title,
    date = LocalDate.parse(date),
    startTime = startTime?.let(LocalTime::parse),
    endTime = endTime?.let(LocalTime::parse),
    location = location,
    memo = memo,
    members = members.map { member ->
        CalendarScheduleMemberUiModel(
            id = member.userId,
            name = member.nickname,
            color = member.color,
            imagePath = groupMembers.firstOrNull { groupMember -> groupMember.userId == member.userId }?.imagePath,
            avatarRes = member.color.toAvatarResource(),
        )
    },
    displayColor = members.firstOrNull()?.color ?: fallbackProfileColor,
)

private fun GroupEvent.toCalendarEventUiModel(
    eventDate: LocalDate,
    fallbackProfileColor: GroupColor,
): CalendarEventUiModel = CalendarEventUiModel(
    id = id,
    title = title,
    startDate = eventDate,
    endDate = eventDate,
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
