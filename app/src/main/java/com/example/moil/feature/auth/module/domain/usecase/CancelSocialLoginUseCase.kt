package com.example.moil.feature.auth.module.domain.usecase

import com.example.moil.feature.auth.module.domain.repository.AuthRepository
import javax.inject.Inject

class CancelSocialLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    // Provider가 취소·실패 callback을 보냈을 때 현재 OAuth 시도를 폐기합니다.
    operator fun invoke() {
        authRepository.cancelSocialLoginAttempt()
    }
}
