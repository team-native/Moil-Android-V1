package com.example.moil.feature.auth.module.data.remote

import com.example.moil.core.network.NetworkResult
import com.example.moil.feature.auth.module.data.dto.ChangePasswordRequestDto
import com.example.moil.feature.auth.module.data.dto.DeleteAccountRequestDto
import com.example.moil.feature.auth.module.data.dto.LoginRequestDto
import com.example.moil.feature.auth.module.data.dto.PasswordSessionRequestDto
import com.example.moil.feature.auth.module.data.dto.SendCodeRequestDto
import com.example.moil.feature.auth.module.data.dto.TokenResponseDto
import com.example.moil.feature.auth.module.data.dto.UpdateProfileRequestDto
import com.example.moil.feature.auth.module.data.dto.UserProfileResponseDto
import com.example.moil.feature.auth.module.data.dto.VerificationResponseDto
import com.example.moil.feature.auth.module.data.dto.VerifiedSessionResponseDto
import com.example.moil.feature.auth.module.data.dto.VerifyCodeRequestDto

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
