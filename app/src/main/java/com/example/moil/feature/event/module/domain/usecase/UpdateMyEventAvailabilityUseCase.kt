package com.example.moil.feature.event.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.event.module.domain.model.EventAvailabilityTimeSlot
import com.example.moil.feature.event.module.domain.repository.EventRepository
import javax.inject.Inject

class UpdateMyEventAvailabilityUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    suspend operator fun invoke(
        eventId: Long,
        date: String,
        timeSlots: List<EventAvailabilityTimeSlot>,
    ): MoilResult<Unit> = repository.updateMyEventAvailability(eventId, date, timeSlots)
}
