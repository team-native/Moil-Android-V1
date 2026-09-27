package com.example.moil.feature.event.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.event.module.domain.model.EventAttendanceChoice
import com.example.moil.feature.event.module.domain.model.EventAttendanceUpdate
import com.example.moil.feature.event.module.domain.repository.EventRepository
import javax.inject.Inject

class UpdateEventAttendanceUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    suspend operator fun invoke(
        eventId: Long,
        choice: EventAttendanceChoice,
    ): MoilResult<EventAttendanceUpdate> = repository.updateEventAttendance(eventId, choice)
}
