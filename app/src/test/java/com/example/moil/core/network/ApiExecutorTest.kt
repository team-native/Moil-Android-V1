package com.example.moil.core.network

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class ApiExecutorTest {
    private val apiExecutor = ApiExecutor(Json { ignoreUnknownKeys = true })

    @Test
    fun `서버 업무 실패 envelope을 ServerError로 변환한다`() = runBlocking {
        val result = apiExecutor.execute<String> {
            Response.success(ApiEnvelopeDto(success = false, status = 400, message = "잘못된 요청", data = null))
        }

        assertTrue(result is NetworkResult.ServerError)
        result as NetworkResult.ServerError
        assertEquals(400, result.status)
        assertEquals("잘못된 요청", result.message)
    }

    @Test
    fun `HTTP 실패를 HttpError로 분리한다`() = runBlocking {
        val result = apiExecutor.execute<String> {
            Response.error(401, "unauthorized".toResponseBody())
        }

        assertTrue(result is NetworkResult.HttpError)
        result as NetworkResult.HttpError
        assertEquals(401, result.code)
    }

    @Test
    fun `HTTP 실패 body의 업무 오류 envelope은 ServerError로 변환한다`() = runBlocking {
        val result = apiExecutor.execute<String> {
            Response.error(
                401,
                "{\"success\":false,\"status\":401,\"message\":\"로그인되어 있지 않습니다.\",\"data\":null}".toResponseBody(),
            )
        }

        assertTrue(result is NetworkResult.ServerError)
        result as NetworkResult.ServerError
        assertEquals(401, result.status)
        assertEquals("로그인되어 있지 않습니다.", result.message)
    }

    @Test
    fun `성공 envelope의 data를 반환한다`() = runBlocking {
        val result = apiExecutor.execute {
            Response.success(ApiEnvelopeDto(success = true, status = 0, message = "ok", data = "result"))
        }

        assertEquals(NetworkResult.Success("result"), result)
    }
}
