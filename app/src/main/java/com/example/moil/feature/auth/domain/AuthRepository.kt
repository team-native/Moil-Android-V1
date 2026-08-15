package com.example.moil.feature.auth.domain

import com.example.moil.core.domain.MoilResult

interface AuthRepository {
    suspend fun updateProfileName(name: String): MoilResult<UserProfile>
    suspend fun sendCode(name: String?, email: String, step: VerificationStep): MoilResult<Verification>
    suspend fun verifyCode(verifyId: String, code: String): MoilResult<VerifiedSession>
    suspend fun confirmSignUp(
        sessionId: String,
        password: String,
        passwordConfirmation: String,
        userName: String,
    ): MoilResult<AuthSession>
    suspend fun login(email: String, password: String): MoilResult<AuthSession>
    suspend fun resetPassword(sessionId: String, password: String, passwordConfirmation: String): MoilResult<Unit>
    suspend fun changePassword(origin: String, newPassword: String, passwordConfirmation: String): MoilResult<Unit>
    suspend fun logout(): MoilResult<Unit>
    suspend fun deleteAccount(email: String, password: String, leaveData: Boolean): MoilResult<Unit>
}
