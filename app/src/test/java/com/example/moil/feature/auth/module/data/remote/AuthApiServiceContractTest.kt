package com.example.moil.feature.auth.module.data.remote

import com.example.moil.feature.auth.module.data.dto.ChangePasswordRequestDto
import com.example.moil.feature.auth.module.data.dto.DeleteAccountRequestDto
import com.example.moil.feature.auth.module.data.dto.LoginRequestDto
import com.example.moil.feature.auth.module.data.dto.PasswordSessionRequestDto
import com.example.moil.feature.auth.module.data.dto.RefreshTokenRequestDto
import com.example.moil.feature.auth.module.data.dto.SendCodeRequestDto
import com.example.moil.feature.auth.module.data.dto.UpdateProfileRequestDto
import com.example.moil.feature.auth.module.data.dto.VerificationStepDto
import com.example.moil.feature.auth.module.data.dto.VerifyCodeRequestDto
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class AuthApiServiceContractTest {
    private val mockWebServer = MockWebServer()
    private lateinit var publicAuthApiService: PublicAuthApiService
    private lateinit var authenticatedAuthApiService: AuthenticatedAuthApiService
    private lateinit var refreshAuthApiService: RefreshAuthApiService

    @Before
    fun setUp() {
        mockWebServer.start()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()))
            .build()
        publicAuthApiService = retrofit.create(PublicAuthApiService::class.java)
        authenticatedAuthApiService = retrofit.create(AuthenticatedAuthApiService::class.java)
        refreshAuthApiService = retrofit.create(RefreshAuthApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    fun `공개 인증 endpoint는 명세의 path와 body를 사용하고 Authorization을 보내지 않는다`() = runBlocking {
        enqueueEnvelope("{\"verifyId\":\"verify-1\"}")
        publicAuthApiService.sendCode(SendCodeRequestDto("모일", "user@example.com", VerificationStepDto.SignUp))
        assertRequest("POST", "/auth/send-code", "\"step\":\"SIGNUP\"")

        enqueueEnvelope("{\"sessionId\":\"session-1\"}")
        publicAuthApiService.verifyCode(VerifyCodeRequestDto("verify-1", "123456"))
        assertRequest("POST", "/auth/verify-code", "\"verifyId\":\"verify-1\"")

        enqueueEnvelope("{\"accessToken\":\"access\",\"refreshToken\":\"refresh\"}")
        publicAuthApiService.confirmSignUp(PasswordSessionRequestDto("session-1", "password12", "password12"))
        assertRequest("POST", "/auth/confirm", "\"pwd\":\"password12\"")

        enqueueEnvelope("{\"accessToken\":\"access\",\"refreshToken\":\"refresh\"}")
        publicAuthApiService.login(LoginRequestDto("user@example.com", "password12"))
        assertRequest("POST", "/auth/login", "\"email\":\"user@example.com\"")

        enqueueEnvelope("null")
        publicAuthApiService.resetPassword(PasswordSessionRequestDto("session-1", "password12", "password12"))
        assertRequest("POST", "/auth/reset-password", "\"sessionId\":\"session-1\"")
    }

    @Test
    fun `refresh endpoint는 refresh_token body를 사용한다`() {
        enqueueEnvelope("{\"accessToken\":\"new-access\",\"refreshToken\":\"new-refresh\"}")

        refreshAuthApiService.refresh(RefreshTokenRequestDto("old-refresh")).execute()

        assertRequest("POST", "/auth/refresh", "\"refresh_token\":\"old-refresh\"")
    }

    @Test
    fun `인증 사용자 인증 endpoint는 명세의 path와 body를 사용한다`() = runBlocking {
        enqueueEnvelope("{\"userId\":1,\"name\":\"네이티브\",\"email\":\"native@example.com\"}")
        val profileResponse = authenticatedAuthApiService.updateProfile(UpdateProfileRequestDto("네이티브"))
        assertEquals("네이티브", profileResponse.body()?.data?.name)
        assertAuthenticatedRequest("PATCH", "/auth/profile", "\"name\":\"네이티브\"")

        enqueueEnvelope("null")
        authenticatedAuthApiService.changePassword(ChangePasswordRequestDto("old-password", "new-password", "new-password"))
        assertAuthenticatedRequest("POST", "/auth/change-password", "\"newpwd\":\"new-password\"")

        enqueueEnvelope("null")
        authenticatedAuthApiService.logout()
        assertAuthenticatedRequest("POST", "/auth/logout", null)

        enqueueEnvelope("null")
        authenticatedAuthApiService.deleteAccount(DeleteAccountRequestDto("user@example.com", "password12", true))
        assertAuthenticatedRequest("POST", "/auth/delete-account", "\"leftData\":true")
    }

    private fun enqueueEnvelope(data: String) {
        mockWebServer.enqueue(
            MockResponse.Builder()
                .code(200)
                .body("{\"success\":true,\"status\":0,\"message\":\"ok\",\"data\":$data}")
                .build(),
        )
    }

    private fun assertRequest(method: String, path: String, expectedBodyFragment: String) {
        val request = mockWebServer.takeRequest()
        assertEquals(method, request.method)
        assertEquals(path, request.target)
        assertTrue(request.body?.utf8().orEmpty().contains(expectedBodyFragment))
        assertFalse(request.headers.names().contains("Authorization"))
    }

    private fun assertAuthenticatedRequest(method: String, path: String, expectedBodyFragment: String?) {
        val request = mockWebServer.takeRequest()
        assertEquals(method, request.method)
        assertEquals(path, request.target)
        if (expectedBodyFragment != null) {
            assertTrue(request.body?.utf8().orEmpty().contains(expectedBodyFragment))
        }
    }
}
