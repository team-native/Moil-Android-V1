package com.example.moil.feature.calendar.viewmodel

import com.example.moil.core.domain.MoilError
import com.example.moil.feature.event.module.domain.model.EventAttendanceStatus
import com.example.moil.feature.group.module.domain.model.GroupColor
import java.time.LocalDate
import java.time.LocalTime

/** 참석·가능 시간 섹션의 서버 조회 상태다. */
enum class EventSectionLoadState {
    Loading,
    Loaded,
    Failed,
}

/** 일정 상세 시트의 참석 여부 섹션 표시 모델이다. */
data class EventAttendanceUiModel(
    val loadState: EventSectionLoadState = EventSectionLoadState.Loading,
    val myStatus: EventAttendanceStatus? = null,
    val attendingCount: Int = 0,
    val declinedCount: Int = 0,
    val pendingCount: Int = 0,
    val isMutating: Boolean = false,
    val mutationError: MoilError? = null,
)

/** 가능 시간 화면의 탭이다. */
enum class EventAvailabilityTab {
    Together,
    Mine,
}

/**
 * 가능 시간 화면에서 사용자가 조작하는 상태다. 서버 응답은 [EventAvailabilityDisplayModel]로 따로 받는다.
 *
 * [selectedSlotStarts]는 "내 시간 입력" 탭에서 고른 30분 칸의 시작 시각이다.
 * 서버의 내 응답이 도착하면 한 번만 이 값으로 채우고([isMySelectionInitialized]), 이후에는 사용자의 선택을 유지한다.
 */
data class EventAvailabilityUiState(
    val eventId: Long? = null,
    val eventTitle: String = "",
    val date: LocalDate? = null,
    val selectedTab: EventAvailabilityTab = EventAvailabilityTab.Together,
    val selectedSlotStarts: Set<LocalTime> = emptySet(),
    val isMySelectionInitialized: Boolean = false,
    val focusedSlotStart: LocalTime? = null,
)

/** 가능 시간 격자 한 칸(30분)의 표시 모델이다. */
data class EventAvailabilityCellUiModel(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val availableCount: Int,
    val isAvailableForEveryone: Boolean,
    val availableMembers: List<EventAvailabilityMemberUiModel>,
)

data class EventAvailabilityMemberUiModel(
    val userId: Long,
    val nickname: String,
    val color: GroupColor,
)

/** 모두 가능하거나 가장 많이 가능한 시간 구간이다. */
data class EventAvailabilityRangeUiModel(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val availableCount: Int,
)

/** 서버 가능 시간 응답을 화면 격자와 요약으로 바꾼 표시 모델이다. */
data class EventAvailabilityDisplayModel(
    val loadState: EventSectionLoadState = EventSectionLoadState.Loading,
    val cells: List<EventAvailabilityCellUiModel> = emptyList(),
    val participantCount: Int = 0,
    val respondedCount: Int = 0,
    val everyoneRanges: List<EventAvailabilityRangeUiModel> = emptyList(),
    val bestRange: EventAvailabilityRangeUiModel? = null,
    val mySlotStarts: Set<LocalTime> = emptySet(),
    val hasMyAvailability: Boolean = false,
    val isMutating: Boolean = false,
    val mutationError: MoilError? = null,
)
