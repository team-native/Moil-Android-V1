package com.example.moil.feature.image.module.data.remote

import com.example.moil.core.network.ApiEnvelopeDto
import com.example.moil.feature.image.module.data.dto.ImageUploadResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageApiService {
    @Multipart
    @POST("image-upload")
    suspend fun uploadProfileImage(
        @Part image: MultipartBody.Part,
    ): Response<ApiEnvelopeDto<ImageUploadResponseDto>>
}
