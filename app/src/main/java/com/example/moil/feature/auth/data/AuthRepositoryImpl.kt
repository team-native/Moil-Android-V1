package com.example.moil.feature.auth.data

import com.example.moil.core.domain.MoilResult
import com.example.moil.core.domain.mapToDomain
import com.example.moil.core.network.SessionManager
import com.example.moil.core.network.SessionTokens
import com.example.moil.feature.auth.data.remote.AuthRemoteDataSource
import com.example.moil.feature.auth.data.remote.ChangePasswordRequestDto
import com.example.moil.feature.auth.data.remote.DeleteAccountRequestDto
import com.example.moil.feature.auth.data.remote.LoginRequestDto
import com.example.moil.feature.auth.data.remote.PasswordSessionRequestDto
import com.example.moil.feature.auth.data.remote.SendCodeRequestDto
import com.example.moil.feature.auth.data.remote.UpdateProfileRequestDto
import com.example.moil.feature.auth.data.remote.UserProfileResponseDto
import com.example.moil.feature.auth.data.remote.VerificationStepDto
import com.example.moil.feature.auth.data.remote.VerifyCodeRequestDto
import com.example.moil.feature.auth.domain.AuthRepository
import com.example.moil.feature.auth.domain.AuthSession
import com.example.moil.feature.auth.domain.Verification
import com.example.moil.feature.auth.domain.VerificationStep
import com.example.moil.feature.auth.domain.VerifiedSession
import com.example.moil.feature.auth.domain.UserProfile
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val sessionManager: SessionManager,
) : AuthRepository {
    // 마이페이지 프로필 저장 이벤트에서 서버 이름 변경 결과를 Domain 모델로 변환합니다.
    override suspend fun updateProfileName(name: String): MoilResult<UserProfile> = authRemoteDataSource
        .updateProfile(UpdateProfileRequestDto(name = name))
        .mapToDomain { response -> response.toDomain() }

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
            .mapToDomain { AuthSession(it.accessToken, it.refreshToken, userName) }
        saveSessionIfSuccessful(result)
        return result
    }

    override suspend fun login(email: String, password: String): MoilResult<AuthSession> {
        val result = authRemoteDataSource.login(LoginRequestDto(email, password))
            .mapToDomain { AuthSession(it.accessToken, it.refreshToken, it.name) }
        saveSessionIfSuccessful(result)
        return result
    }

    override suspend fun resetPassword(sessionId: String, password: String, passwordConfirmation: String): MoilResult<Unit> = authRemoteDataSource
        .resetPassword(PasswordSessionRequestDto(sessionId, password, passwordConfirmation))
        .mapToDomain { Unit }

    override suspend fun changePassword(origin: String, newPassword: String, passwordConfirmation: String): MoilResult<Unit> = authRemoteDataSource
        .changePassword(ChangePasswordRequestDto(origin, newPassword, passwordConfirmation))
        .mapToDomain { Unit }

    override suspend fun logout(): MoilResult<Unit> = authRemoteDataSource
        .logout()
        .mapToDomain { Unit }

    override suspend fun deleteAccount(email: String, password: String, leaveData: Boolean): MoilResult<Unit> {
        val result = authRemoteDataSource.deleteAccount(DeleteAccountRequestDto(email, password, leaveData)).mapToDomain { Unit }
        if (result is MoilResult.Success) sessionManager.expireSession()
        return result
    }

    private fun saveSessionIfSuccessful(result: MoilResult<AuthSession>) {
        if (result is MoilResult.Success) {
            sessionManager.save(
                tokens = SessionTokens(
                    accessToken = result.value.accessToken,
                    refreshToken = result.value.refreshToken,
                ),
                userName = result.value.userName,
            )
        }
}

private fun UserProfileResponseDto.toDomain(): UserProfile = UserProfile(
    userId = userId,
    name = name,
    email = email,
)

private fun VerificationStep.toDto(): VerificationStepDto = when (this) {
        VerificationStep.SignUp -> VerificationStepDto.SignUp
        VerificationStep.Reset -> VerificationStepDto.Reset
    }
}
