package com.example.moil.feature.event.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.event.module.domain.model.GroupEvent
import com.example.moil.feature.event.module.domain.repository.EventRepository
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
