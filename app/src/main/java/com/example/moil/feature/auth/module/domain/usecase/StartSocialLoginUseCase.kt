package com.example.moil.feature.auth.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.OAuthAuthorizationRequest
import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider
import com.example.moil.feature.auth.module.domain.repository.AuthRepository
import javax.inject.Inject

class StartSocialLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    // 소셜 로그인 시도마다 새로운 state와 PKCE verifier를 생성한 인증 URL을 반환합니다.
    suspend operator fun invoke(provider: SocialLoginProvider): MoilResult<OAuthAuthorizationRequest> =
        authRepository.startSocialLogin(provider)
}
