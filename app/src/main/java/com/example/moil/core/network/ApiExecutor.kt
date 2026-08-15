package com.example.moil.core.network

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import retrofit2.Response

class ApiExecutor @Inject constructor(
    private val json: Json,
) {
    suspend fun <T> execute(
        request: suspend () -> Response<ApiEnvelopeDto<T>>,
    ): NetworkResult<T> {
        return try {
            val response = request()
            val envelope = response.body()

            if (!response.isSuccessful) {
                toHttpOrServerError(response)
            } else if (envelope == null) {
                NetworkResult.NetworkError(
                    cause = IllegalStateException("응답 본문이 없습니다."),
                )
            } else if (!envelope.success) {
                NetworkResult.ServerError(
                    status = envelope.status,
                    message = envelope.message,
                )
            } else if (envelope.data == null) {
                NetworkResult.NetworkError(
                    cause = IllegalStateException("성공 응답에 data가 없습니다."),
                )
            } else {
                NetworkResult.Success(envelope.data)
            }
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (ioException: IOException) {
            NetworkResult.NetworkError(ioException)
        } catch (exception: Exception) {
            NetworkResult.NetworkError(exception)
        }
    }

    suspend fun executeVoid(
        request: suspend () -> Response<ApiEnvelopeDto<Unit>>,
    ): NetworkResult<Unit> {
        return try {
            val response = request()
            val envelope = response.body()

            if (!response.isSuccessful) {
                toHttpOrServerError(response)
            } else if (envelope == null) {
                NetworkResult.NetworkError(
                    cause = IllegalStateException("응답 본문이 없습니다."),
                )
            } else if (!envelope.success) {
                NetworkResult.ServerError(
                    status = envelope.status,
                    message = envelope.message,
                )
            } else {
                NetworkResult.Success(Unit)
            }
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (ioException: IOException) {
            NetworkResult.NetworkError(ioException)
        } catch (exception: Exception) {
            NetworkResult.NetworkError(exception)
        }
    }

    private fun <T> toHttpOrServerError(response: Response<T>): NetworkResult<Nothing> {
        val errorEnvelope = response.errorBody()
            ?.string()
            ?.let { body -> runCatching { json.decodeFromString<ApiEnvelopeDto<JsonElement>>(body) }.getOrNull() }

        return if (errorEnvelope?.success == false) {
            NetworkResult.ServerError(
                status = errorEnvelope.status,
                message = errorEnvelope.message,
            )
        } else {
            NetworkResult.HttpError(
                code = response.code(),
                message = response.message(),
            )
        }
    }
}
