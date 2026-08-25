package com.example.moil.feature.group.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.group.module.domain.model.InviteVerification
import com.example.moil.feature.group.module.domain.repository.GroupRepository
import javax.inject.Inject

class VerifyInviteUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(inviteCode: String): MoilResult<InviteVerification> =
        repository.verifyInvite(inviteCode)
}
