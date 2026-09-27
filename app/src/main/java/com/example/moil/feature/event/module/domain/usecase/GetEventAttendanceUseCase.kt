package com.example.moil.feature.event.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.event.module.domain.model.EventAttendance
import com.example.moil.feature.event.module.domain.repository.EventRepository
import javax.inject.Inject

class GetEventAttendanceUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    suspend operator fun invoke(eventId: Long): MoilResult<EventAttendance> =
        repository.getEventAttendance(eventId)
}
