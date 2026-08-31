package com.example.moil.feature.auth.module.domain.usecase

import com.example.moil.feature.auth.module.domain.repository.AuthRepository
import com.example.moil.feature.auth.module.domain.model.SocialLoginFailure
import javax.inject.Inject

class CancelSocialLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    // Provider가 반환한 state가 현재 시도와 일치할 때만 OAuth 시도를 폐기합니다.
    operator fun invoke(failure: SocialLoginFailure) {
        authRepository.cancelSocialLoginAttempt(failure.provider, failure.state)
    }
}
