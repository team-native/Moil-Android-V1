package com.example.moil.feature.auth.data.remote

import com.example.moil.core.network.ApiEnvelopeDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PublicAuthApiService {
    @POST("auth/send-code")
    suspend fun sendCode(@Body request: SendCodeRequestDto): Response<ApiEnvelopeDto<VerificationResponseDto>>

    @POST("auth/verify-code")
    suspend fun verifyCode(@Body request: VerifyCodeRequestDto): Response<ApiEnvelopeDto<VerifiedSessionResponseDto>>

    @POST("auth/confirm")
    suspend fun confirmSignUp(@Body request: PasswordSessionRequestDto): Response<ApiEnvelopeDto<TokenResponseDto>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<ApiEnvelopeDto<TokenResponseDto>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: PasswordSessionRequestDto): Response<ApiEnvelopeDto<Unit>>
}

interface AuthenticatedAuthApiService {
    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): Response<ApiEnvelopeDto<Unit>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiEnvelopeDto<Unit>>

    @POST("auth/delete-account")
    suspend fun deleteAccount(@Body request: DeleteAccountRequestDto): Response<ApiEnvelopeDto<Unit>>
}

interface RefreshAuthApiService {
    @POST("auth/refresh")
    fun refresh(@Body request: RefreshTokenRequestDto): Call<ApiEnvelopeDto<TokenResponseDto>>
}
