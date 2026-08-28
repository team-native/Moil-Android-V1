package com.example.moil.feature.auth.module.data.repository

import com.example.moil.core.domain.MoilResult
import com.example.moil.core.domain.mapToDomain
import com.example.moil.core.network.SessionManager
import com.example.moil.core.network.SessionTokens
import com.example.moil.feature.auth.module.data.dto.ChangePasswordRequestDto
import com.example.moil.feature.auth.module.data.dto.DeleteAccountRequestDto
import com.example.moil.feature.auth.module.data.dto.LoginRequestDto
import com.example.moil.feature.auth.module.data.dto.PasswordSessionRequestDto
import com.example.moil.feature.auth.module.data.dto.SendCodeRequestDto
import com.example.moil.feature.auth.module.data.dto.UpdateProfileRequestDto
import com.example.moil.feature.auth.module.data.dto.VerifyCodeRequestDto
import com.example.moil.feature.auth.module.data.mapper.toDomain
import com.example.moil.feature.auth.module.data.mapper.toDto
import com.example.moil.feature.auth.module.data.mapper.toUserProfileOrNull
import com.example.moil.feature.auth.module.data.remote.AuthRemoteDataSource
import com.example.moil.feature.auth.module.data.dto.SocialLoginCallbackRequestDto
import com.example.moil.feature.auth.module.data.oauth.OAuthAuthorizationRequestFactory
import com.example.moil.feature.auth.module.domain.model.AuthSession
import com.example.moil.feature.auth.module.domain.model.UserProfile
import com.example.moil.feature.auth.module.domain.model.Verification
import com.example.moil.feature.auth.module.domain.model.VerificationStep
import com.example.moil.feature.auth.module.domain.model.VerifiedSession
import com.example.moil.feature.auth.module.domain.model.OAuthAuthorizationRequest
import com.example.moil.feature.auth.module.domain.model.SocialLoginCallback
import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider
import com.example.moil.feature.auth.module.domain.repository.AuthRepository
import com.example.moil.feature.auth.module.domain.repository.CurrentUserProfileStore
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val sessionManager: SessionManager,
    private val currentUserProfileStore: CurrentUserProfileStore,
    private val oauthAuthorizationRequestFactory: OAuthAuthorizationRequestFactory,
) : AuthRepository {
    // 마이페이지 프로필 저장 이벤트에서 서버 이름 변경 결과를 Domain 모델로 변환합니다.
    override suspend fun updateProfileName(name: String): MoilResult<UserProfile> {
        val result = authRemoteDataSource
            .updateProfile(UpdateProfileRequestDto(name = name))
            .mapToDomain { response -> response.toDomain() }

        if (result is MoilResult.Success) {
            currentUserProfileStore.save(result.value)
        }

        return result
    }

    override suspend fun sendCode(name: String?, email: String, step: VerificationStep): MoilResult<Verification> = authRemoteDataSource
        .sendCode(SendCodeRequestDto(name = name, email = email, step = step.toDto()))
        .mapToDomain { Verification(verifyId = it.verifyId) }

    override suspend fun verifyCode(verifyId: String, code: String): MoilResult<VerifiedSession> = authRemoteDataSource
        .verifyCode(VerifyCodeRequestDto(verifyId = verifyId, code = code))
        .mapToDomain { VerifiedSession(sessionId = it.sessionId) }

    override suspend fun confirmSignUp(
        sessionId: String,
        password: String,
        passwordConfirmation: String,
        userName: String,
    ): MoilResult<AuthSession> {
        val result = authRemoteDataSource.confirmSignUp(PasswordSessionRequestDto(sessionId, password, passwordConfirmation))
            .mapToDomain { response ->
                AuthSession(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    profile = response.toUserProfileOrNull(fallbackName = userName),
                )
            }
        saveSessionIfSuccessful(result)
        return result
    }

    override suspend fun login(email: String, password: String): MoilResult<AuthSession> {
        val result = authRemoteDataSource.login(LoginRequestDto(email, password))
            .mapToDomain { response ->
                AuthSession(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    profile = response.toUserProfileOrNull(),
                )
            }
        saveSessionIfSuccessful(result)
        return result
    }

    override suspend fun startSocialLogin(provider: SocialLoginProvider): MoilResult<OAuthAuthorizationRequest> =
        oauthAuthorizationRequestFactory.create(provider)

    override suspend fun completeSocialLogin(callback: SocialLoginCallback): MoilResult<AuthSession> {
        val codeVerifier = oauthAuthorizationRequestFactory.consumeCodeVerifier(callback)
            ?: return MoilResult.Failure(
                com.example.moil.core.domain.MoilError.Configuration("소셜 로그인 요청을 확인할 수 없습니다."),
            )
        val result = authRemoteDataSource
            .completeSocialLogin(
                socialLoginType = callback.provider.wireValue,
                request = SocialLoginCallbackRequestDto(
                    code = callback.code,
                    user = callback.user,
                    codeVerifier = codeVerifier,
                ),
            )
            .mapToDomain { response ->
                AuthSession(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    profile = response.toUserProfileOrNull(),
                )
            }
        saveSessionIfSuccessful(result)
        return result
    }

    override suspend fun resetPassword(sessionId: String, password: String, passwordConfirmation: String): MoilResult<Unit> = authRemoteDataSource
        .resetPassword(PasswordSessionRequestDto(sessionId, password, passwordConfirmation))
        .mapToDomain { Unit }

    override suspend fun changePassword(origin: String, newPassword: String, passwordConfirmation: String): MoilResult<Unit> = authRemoteDataSource
        .changePassword(ChangePasswordRequestDto(origin, newPassword, passwordConfirmation))
        .mapToDomain { Unit }

    override suspend fun logout(): MoilResult<Unit> {
        val result = authRemoteDataSource
            .logout()
            .mapToDomain { Unit }

        if (result is MoilResult.Success) {
            currentUserProfileStore.clear()
            sessionManager.expireSession()
        }

        return result
    }

    override suspend fun deleteAccount(email: String, password: String, leaveData: Boolean): MoilResult<Unit> {
        val result = authRemoteDataSource.deleteAccount(DeleteAccountRequestDto(email, password, leaveData)).mapToDomain { Unit }
        if (result is MoilResult.Success) {
            currentUserProfileStore.clear()
            sessionManager.expireSession()
        }
        return result
    }

    private fun saveSessionIfSuccessful(result: MoilResult<AuthSession>) {
        if (result is MoilResult.Success) {
            sessionManager.save(
                tokens = SessionTokens(
                    accessToken = result.value.accessToken,
                    refreshToken = result.value.refreshToken,
                ),
            )
            result.value.profile?.let(currentUserProfileStore::save)
        }
    }
}
