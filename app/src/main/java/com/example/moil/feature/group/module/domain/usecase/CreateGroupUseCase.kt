package com.example.moil.feature.group.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.GroupSummary
import com.example.moil.feature.group.module.domain.repository.GroupRepository
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(name: String, nickname: String, color: GroupColor): MoilResult<GroupSummary> =
        repository.createGroup(name, nickname, color)
}
