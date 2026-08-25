package com.example.moil.feature.group.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.group.module.domain.repository.GroupRepository
import javax.inject.Inject

class RenameGroupUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(groupId: Long, name: String): MoilResult<Unit> = repository.renameGroup(groupId, name)
}
