package com.example.moil.feature.auth.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.VerifiedSession
import com.example.moil.feature.auth.module.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyCodeUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(verifyId: String, code: String): MoilResult<VerifiedSession> =
        repository.verifyCode(verifyId, code)
}
