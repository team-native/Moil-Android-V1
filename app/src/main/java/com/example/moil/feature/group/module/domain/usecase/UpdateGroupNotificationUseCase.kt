package com.example.moil.feature.group.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.group.module.domain.repository.GroupRepository
import javax.inject.Inject

class UpdateGroupNotificationUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(groupId: Long, enabled: Boolean): MoilResult<Unit> =
        repository.updateNotification(groupId, enabled)
}
