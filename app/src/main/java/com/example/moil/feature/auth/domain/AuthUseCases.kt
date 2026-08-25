package com.example.moil.feature.auth.domain

import com.example.moil.core.domain.MoilResult
import javax.inject.Inject

class UpdateProfileNameUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    // 프로필 편집 화면의 저장 이벤트에서 사용자 이름을 서버에 반영합니다.
    // 성공 시 변경된 사용자 프로필을, 실패 시 공통 Domain 오류를 반환합니다.
    suspend operator fun invoke(name: String): MoilResult<UserProfile> = repository.updateProfileName(name)
}

class SendVerificationCodeUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(name: String?, email: String, step: VerificationStep): MoilResult<Verification> =
        repository.sendCode(name, email, step)
}

class VerifyCodeUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(verifyId: String, code: String): MoilResult<VerifiedSession> =
        repository.verifyCode(verifyId, code)
}

class ConfirmSignUpUseCase @Inject constructor(private val repository: AuthRepository) {
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

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): MoilResult<AuthSession> =
        repository.login(email, password)
}

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(sessionId: String, password: String, confirmation: String): MoilResult<Unit> =
        repository.resetPassword(sessionId, password, confirmation)
}

class ChangePasswordUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(origin: String, password: String, confirmation: String): MoilResult<Unit> =
        repository.changePassword(origin, password, confirmation)
}

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): MoilResult<Unit> = repository.logout()
}

class DeleteAccountUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String, leaveData: Boolean): MoilResult<Unit> =
        repository.deleteAccount(email, password, leaveData)
}
