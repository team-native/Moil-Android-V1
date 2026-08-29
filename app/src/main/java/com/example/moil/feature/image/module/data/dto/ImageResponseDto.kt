package com.example.moil.feature.image.module.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageUploadResponseDto(
    @SerialName("imagePath") val imagePath: String,
)
