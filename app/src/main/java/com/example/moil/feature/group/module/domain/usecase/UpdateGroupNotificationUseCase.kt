package com.example.moil.feature.group.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.group.module.domain.repository.GroupRepository
import javax.inject.Inject

class UpdateGroupNotificationUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    // 그룹 알림 스위치에서 호출되며, 성공하면 서버에 저장된 알림 수신 여부를 돌려준다.
    suspend operator fun invoke(groupId: Long, enabled: Boolean): MoilResult<Boolean> =
        repository.updateNotification(groupId, enabled)
}
