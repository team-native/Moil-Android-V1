package com.example.moil.feature.auth.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.Verification
import com.example.moil.feature.auth.module.domain.model.VerificationStep
import com.example.moil.feature.auth.module.domain.repository.AuthRepository
import javax.inject.Inject

class SendVerificationCodeUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(name: String?, email: String, step: VerificationStep): MoilResult<Verification> =
        repository.sendCode(name, email, step)
}
