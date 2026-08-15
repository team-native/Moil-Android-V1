package com.example.moil.feature.event.domain

import com.example.moil.core.domain.MoilResult
import java.time.YearMonth
import javax.inject.Inject

class GetGroupEventsUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    suspend operator fun invoke(
        groupId: Long,
        month: YearMonth,
    ): MoilResult<List<GroupEvent>> = repository.getGroupEvents(groupId, month)
}

class CreateEventUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    suspend operator fun invoke(
        event: GroupEvent,
        groupId: Long,
        memberIds: List<Long>,
    ): MoilResult<Long> = repository.createEvent(event, groupId, memberIds)
}

class GetEventUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    suspend operator fun invoke(eventId: Long): MoilResult<GroupEvent> =
        repository.getEvent(eventId)
}

class UpdateEventUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    suspend operator fun invoke(
        eventId: Long,
        event: GroupEvent,
        memberIds: List<Long>,
    ): MoilResult<Unit> = repository.updateEvent(eventId, event, memberIds)
}

class DeleteEventUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    suspend operator fun invoke(eventId: Long): MoilResult<Unit> = repository.deleteEvent(eventId)
}
