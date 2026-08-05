package com.example.moil.feature.event.domain

import com.example.moil.core.domain.MoilResult
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveGroupEventsUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    operator fun invoke(groupId: Long, month: YearMonth): Flow<List<GroupEvent>> =
        repository.observeGroupEvents(groupId, month)
}

class ObserveMonthlyEventCountUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    operator fun invoke(groupId: Long, month: YearMonth): Flow<Int> =
        repository.observeMonthlyEventCount(groupId, month)
}

class RefreshGroupEventsUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    suspend operator fun invoke(groupId: Long, month: YearMonth): MoilResult<Unit> =
        repository.refreshGroupEvents(groupId, month)
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
