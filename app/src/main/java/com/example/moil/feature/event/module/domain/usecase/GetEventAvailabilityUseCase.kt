package com.example.moil.feature.event.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.event.module.domain.model.EventAvailability
import com.example.moil.feature.event.module.domain.repository.EventRepository
import javax.inject.Inject

class GetEventAvailabilityUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    suspend operator fun invoke(
        eventId: Long,
        date: String,
    ): MoilResult<EventAvailability?> = repository.getEventAvailability(eventId, date)
}
