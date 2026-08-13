package com.example.moil.feature.auth.data.remote

import com.example.moil.core.network.NetworkResult

interface AuthRemoteDataSource {
    suspend fun updateProfile(request: UpdateProfileRequestDto): NetworkResult<UserProfileResponseDto>
    suspend fun sendCode(request: SendCodeRequestDto): NetworkResult<VerificationResponseDto>
    suspend fun verifyCode(request: VerifyCodeRequestDto): NetworkResult<VerifiedSessionResponseDto>
    suspend fun confirmSignUp(request: PasswordSessionRequestDto): NetworkResult<TokenResponseDto>
    suspend fun login(request: LoginRequestDto): NetworkResult<TokenResponseDto>
    suspend fun resetPassword(request: PasswordSessionRequestDto): NetworkResult<Unit>
    suspend fun changePassword(request: ChangePasswordRequestDto): NetworkResult<Unit>
    suspend fun logout(): NetworkResult<Unit>
    suspend fun deleteAccount(request: DeleteAccountRequestDto): NetworkResult<Unit>
}
