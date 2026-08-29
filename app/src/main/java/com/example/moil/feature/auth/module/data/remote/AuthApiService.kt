package com.example.moil.feature.auth.module.data.remote

import com.example.moil.core.network.ApiEnvelopeDto
import com.example.moil.feature.auth.module.data.dto.ChangePasswordRequestDto
import com.example.moil.feature.auth.module.data.dto.DeleteAccountRequestDto
import com.example.moil.feature.auth.module.data.dto.LoginRequestDto
import com.example.moil.feature.auth.module.data.dto.PasswordSessionRequestDto
import com.example.moil.feature.auth.module.data.dto.RefreshTokenRequestDto
import com.example.moil.feature.auth.module.data.dto.SendCodeRequestDto
import com.example.moil.feature.auth.module.data.dto.SocialLoginCallbackRequestDto
import com.example.moil.feature.auth.module.data.dto.TokenResponseDto
import com.example.moil.feature.auth.module.data.dto.UpdateProfileRequestDto
import com.example.moil.feature.auth.module.data.dto.UserProfileResponseDto
import com.example.moil.feature.auth.module.data.dto.VerificationResponseDto
import com.example.moil.feature.auth.module.data.dto.VerifiedSessionResponseDto
import com.example.moil.feature.auth.module.data.dto.VerifyCodeRequestDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

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

    @POST("oauth/{socialLoginType}/token")
    suspend fun completeSocialLogin(
        @Path("socialLoginType") socialLoginType: String,
        @Body request: SocialLoginCallbackRequestDto,
    ): Response<ApiEnvelopeDto<TokenResponseDto>>
}

interface AuthenticatedAuthApiService {
    @PATCH("auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequestDto): Response<ApiEnvelopeDto<UserProfileResponseDto>>

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
