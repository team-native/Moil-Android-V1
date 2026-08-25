package com.example.moil.feature.auth.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.AuthSession
import com.example.moil.feature.auth.module.domain.repository.AuthRepository
import javax.inject.Inject

class ConfirmSignUpUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        sessionId: String,
        password: String,
        confirmation: String,
        userName: String,
    ): MoilResult<AuthSession> = repository.confirmSignUp(
        sessionId = sessionId,
        password = password,
        passwordConfirmation = confirmation,
        userName = userName,
    )
}
