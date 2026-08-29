package com.example.moil.feature.auth.module.domain.repository

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.AuthSession
import com.example.moil.feature.auth.module.domain.model.OAuthAuthorizationRequest
import com.example.moil.feature.auth.module.domain.model.SocialLoginCallback
import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider
import com.example.moil.feature.auth.module.domain.model.Verification
import com.example.moil.feature.auth.module.domain.model.VerificationStep
import com.example.moil.feature.auth.module.domain.model.VerifiedSession
import com.example.moil.feature.auth.module.domain.model.UserProfile

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
    suspend fun startSocialLogin(provider: SocialLoginProvider): MoilResult<OAuthAuthorizationRequest>
    suspend fun completeSocialLogin(callback: SocialLoginCallback): MoilResult<AuthSession>
    fun cancelSocialLoginAttempt()
    suspend fun resetPassword(sessionId: String, password: String, passwordConfirmation: String): MoilResult<Unit>
    suspend fun changePassword(origin: String, newPassword: String, passwordConfirmation: String): MoilResult<Unit>
    suspend fun logout(): MoilResult<Unit>
    suspend fun deleteAccount(email: String, password: String, leaveData: Boolean): MoilResult<Unit>
}
