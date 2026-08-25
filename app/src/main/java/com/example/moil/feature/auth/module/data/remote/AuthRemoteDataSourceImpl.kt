package com.example.moil.feature.auth.module.data.remote

import com.example.moil.core.network.ApiExecutor
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
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val publicAuthApiService: PublicAuthApiService,
    private val authenticatedAuthApiService: AuthenticatedAuthApiService,
    private val apiExecutor: ApiExecutor,
) : AuthRemoteDataSource {
    override suspend fun updateProfile(request: UpdateProfileRequestDto): NetworkResult<UserProfileResponseDto> = apiExecutor.execute {
        authenticatedAuthApiService.updateProfile(request)
    }

    override suspend fun sendCode(request: SendCodeRequestDto): NetworkResult<VerificationResponseDto> = apiExecutor.execute { publicAuthApiService.sendCode(request) }
    override suspend fun verifyCode(request: VerifyCodeRequestDto): NetworkResult<VerifiedSessionResponseDto> = apiExecutor.execute { publicAuthApiService.verifyCode(request) }
    override suspend fun confirmSignUp(request: PasswordSessionRequestDto): NetworkResult<TokenResponseDto> = apiExecutor.execute { publicAuthApiService.confirmSignUp(request) }
    override suspend fun login(request: LoginRequestDto): NetworkResult<TokenResponseDto> = apiExecutor.execute { publicAuthApiService.login(request) }
    override suspend fun resetPassword(request: PasswordSessionRequestDto): NetworkResult<Unit> = apiExecutor.executeVoid { publicAuthApiService.resetPassword(request) }
    override suspend fun changePassword(request: ChangePasswordRequestDto): NetworkResult<Unit> = apiExecutor.executeVoid { authenticatedAuthApiService.changePassword(request) }
    override suspend fun logout(): NetworkResult<Unit> = apiExecutor.executeVoid { authenticatedAuthApiService.logout() }
    override suspend fun deleteAccount(request: DeleteAccountRequestDto): NetworkResult<Unit> = apiExecutor.executeVoid { authenticatedAuthApiService.deleteAccount(request) }
}
