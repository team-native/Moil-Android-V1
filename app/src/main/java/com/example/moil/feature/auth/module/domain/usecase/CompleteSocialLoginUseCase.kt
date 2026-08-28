package com.example.moil.feature.auth.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.AuthSession
import com.example.moil.feature.auth.module.domain.model.SocialLoginCallback
import com.example.moil.feature.auth.module.domain.repository.AuthRepository
import javax.inject.Inject

class CompleteSocialLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    // Provider callback의 code와 state를 서버 교환으로 연결하고 서비스 세션을 반환합니다.
    suspend operator fun invoke(callback: SocialLoginCallback): MoilResult<AuthSession> =
        authRepository.completeSocialLogin(callback)
}
