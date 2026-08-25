package com.example.moil.feature.group.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.group.module.domain.model.GroupRole
import com.example.moil.feature.group.module.domain.repository.GroupRepository
import javax.inject.Inject

class UpdateMemberRolesUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(groupId: Long, roles: Map<Long, GroupRole>): MoilResult<Unit> =
        repository.updateMemberRoles(groupId, roles)
}
