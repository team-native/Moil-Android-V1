package com.example.moil.feature.calendar.presentation.component.schedule

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.moil.R
import com.example.moil.feature.calendar.presentation.CalendarMemberId

internal val scheduleCalendarMembers = listOf(
    ScheduleCalendarMember(
        id = CalendarMemberId.Dad,
        nameRes = R.string.family_member_dad,
        avatarRes = R.drawable.family_avatar_dad,
    ),
    ScheduleCalendarMember(
        id = CalendarMemberId.Mom,
        nameRes = R.string.family_member_mom,
        avatarRes = R.drawable.family_avatar_mom,
    ),
    ScheduleCalendarMember(
        id = CalendarMemberId.Me,
        nameRes = R.string.family_member_me,
        avatarRes = R.drawable.family_avatar_mine,
    ),
    ScheduleCalendarMember(
        id = CalendarMemberId.Sibling,
        nameRes = R.string.family_member_sister,
        avatarRes = R.drawable.family_avatar_sibling,
    ),
)

internal data class ScheduleCalendarMember(
    val id: CalendarMemberId,
    @param:StringRes val nameRes: Int,
    @param:DrawableRes val avatarRes: Int,
)
