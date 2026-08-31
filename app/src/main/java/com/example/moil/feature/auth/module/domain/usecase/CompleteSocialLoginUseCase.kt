package com.example.moil.feature.auth.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.AuthSession
import com.example.moil.feature.auth.module.domain.model.SocialLoginCallback
import com.example.moil.feature.auth.module.domain.repository.AuthRepository
import javax.inject.Inject

class CompleteSocialLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    // 서버가 딥링크로 반환한 토큰을 state 검증 후 서비스 세션으로 저장합니다.
    suspend operator fun invoke(callback: SocialLoginCallback): MoilResult<AuthSession> =
        authRepository.completeSocialLogin(callback)
}
