package com.example.moil.core.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiEnvelopeDto<T>(
    @SerialName("success")
    val success: Boolean,
    @SerialName("status")
    val status: Int,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: T? = null,
)

sealed interface NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>
    data class ServerError(val status: Int, val message: String) : NetworkResult<Nothing>
    data class HttpError(val code: Int, val message: String) : NetworkResult<Nothing>
    data class NetworkError(val cause: Throwable) : NetworkResult<Nothing>
}
