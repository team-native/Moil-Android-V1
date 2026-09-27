package com.example.moil.feature.event.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.event.module.domain.repository.EventRepository
import javax.inject.Inject

class DeleteMyEventAvailabilityUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    suspend operator fun invoke(eventId: Long, date: String): MoilResult<Unit> =
        repository.deleteMyEventAvailability(eventId, date)
}
