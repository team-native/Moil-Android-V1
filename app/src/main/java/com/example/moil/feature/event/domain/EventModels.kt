package com.example.moil.feature.event.domain

import com.example.moil.feature.group.domain.GroupColor

data class GroupEvent(
    val id: Long,
    val title: String,
    val startDate: String,
    val endDate: String,
    val isAllDay: Boolean,
    val startTime: String?,
    val endTime: String?,
    val location: String?,
    val members: List<EventMember>,
) {
    /**
     * 기간형 캘린더 UI가 적용되기 전 단일 날짜 호출부를 안전하게 유지한다.
     * 새 코드에서는 [startDate]와 [endDate]를 사용한다.
     */
    @Deprecated("Use startDate and endDate instead.")
    constructor(
        id: Long,
        title: String,
        date: String,
        isAllDay: Boolean,
        startTime: String?,
        endTime: String?,
        location: String?,
        members: List<EventMember>,
    ) : this(
        id = id,
        title = title,
        startDate = date,
        endDate = date,
        isAllDay = isAllDay,
        startTime = startTime,
        endTime = endTime,
        location = location,
        members = members,
    )

    /** 기간형 UI 전환 전 기존 단일 날짜 조회가 시작일을 표시하도록 호환한다. */
    @Deprecated("Use startDate instead.")
    val date: String
        get() = startDate
}

data class EventMember(val userId: Long, val nickname: String, val color: GroupColor)
