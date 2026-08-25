package com.example.moil.core.network

import com.example.moil.feature.auth.module.data.remote.RefreshAuthApiService
import dagger.Lazy
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class TokenRefreshAuthenticatorTest {
    private val mockWebServer = MockWebServer()
    private val json = Json { ignoreUnknownKeys = true }
    private val sessionManager = FakeSessionManager(
        SessionTokens(accessToken = "expired-access", refreshToken = "refresh-token"),
    )

    @Before
    fun setUp() {
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    fun `첫 401은 토큰을 갱신하고 새 access token으로 원 요청을 한 번 재시도한다`() {
        mockWebServer.enqueue(MockResponse.Builder().code(401).body(errorEnvelope).build())
        mockWebServer.enqueue(MockResponse.Builder().code(200).body(refreshSuccessEnvelope).build())
        mockWebServer.enqueue(MockResponse.Builder().code(200).body("ok").build())

        val response = authenticatedClient().newCall(request()).execute()

        assertTrue(response.isSuccessful)
        response.close()
        assertEquals(SessionTokens("new-access", "new-refresh"), sessionManager.currentTokens())

        assertRequest("/protected", "Bearer expired-access")
        assertRequest("/auth/refresh", "Bearer expired-access")
        assertRequest("/protected", "Bearer new-access")
    }

    @Test
    fun `refresh 업무 실패는 토큰을 삭제하고 원 요청을 재시도하지 않는다`() {
        mockWebServer.enqueue(MockResponse.Builder().code(401).body(errorEnvelope).build())
        mockWebServer.enqueue(MockResponse.Builder().code(401).body(errorEnvelope).build())

        val response = authenticatedClient().newCall(request()).execute()

        assertEquals(401, response.code)
        response.close()
        assertNull(sessionManager.currentTokens())
        assertEquals(2, mockWebServer.requestCount)
    }

    @Test
    fun `refresh 응답 직렬화 실패도 토큰을 삭제하고 원 요청을 재시도하지 않는다`() {
        mockWebServer.enqueue(MockResponse.Builder().code(401).body(errorEnvelope).build())
        mockWebServer.enqueue(MockResponse.Builder().code(200).body("not-json").build())

        val response = authenticatedClient().newCall(request()).execute()

        assertEquals(401, response.code)
        response.close()
        assertNull(sessionManager.currentTokens())
        assertEquals(2, mockWebServer.requestCount)
    }

    @Test
    fun `재시도 요청도 401이면 refresh를 다시 호출하지 않고 토큰을 삭제한다`() {
        mockWebServer.enqueue(MockResponse.Builder().code(401).body(errorEnvelope).build())
        mockWebServer.enqueue(MockResponse.Builder().code(200).body(refreshSuccessEnvelope).build())
        mockWebServer.enqueue(MockResponse.Builder().code(401).body(errorEnvelope).build())

        val response = authenticatedClient().newCall(request()).execute()

        assertEquals(401, response.code)
        response.close()
        assertNull(sessionManager.currentTokens())
        assertEquals(3, mockWebServer.requestCount)
    }

    @Test
    fun `빈 토큰을 반환한 refresh 응답은 세션을 삭제하고 원 요청을 재시도하지 않는다`() {
        mockWebServer.enqueue(MockResponse.Builder().code(401).body(errorEnvelope).build())
        mockWebServer.enqueue(MockResponse.Builder().code(200).body(emptyTokenEnvelope).build())

        val response = authenticatedClient().newCall(request()).execute()

        assertEquals(401, response.code)
        response.close()
        assertNull(sessionManager.currentTokens())
        assertEquals(2, mockWebServer.requestCount)
    }

    private fun authenticatedClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthorizationInterceptor(sessionManager))
        .authenticator(TokenRefreshAuthenticator(sessionManager, Lazy { refreshApiService() }))
        .build()

    private fun refreshApiService(): RefreshAuthApiService {
        val refreshClient = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(sessionManager))
            .build()
        return Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(refreshClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RefreshAuthApiService::class.java)
    }

    private fun request(): Request = Request.Builder()
        .url(mockWebServer.url("/protected"))
        .build()

    private fun assertRequest(path: String, authorization: String) {
        val request = mockWebServer.takeRequest()
        assertEquals(path, request.target)
        assertEquals(authorization, request.headers["Authorization"])
    }

    private class FakeSessionManager(
        private var tokens: SessionTokens?,
    ) : SessionManager {
        private val mutableSessionState = MutableStateFlow(tokens.toSessionState())
        private val mutableSessionEvents = MutableSharedFlow<SessionEvent>()

        override val sessionState: StateFlow<SessionState> = mutableSessionState
        override val sessionEvents: SharedFlow<SessionEvent> = mutableSessionEvents

        override fun currentTokens(): SessionTokens? = tokens

        override fun save(tokens: SessionTokens) {
            this.tokens = tokens
            mutableSessionState.value = SessionState.Authenticated
        }

        override fun expireSession() {
            tokens = null
            mutableSessionState.value = SessionState.Unauthenticated
            mutableSessionEvents.tryEmit(SessionEvent.Expired)
        }

        private fun SessionTokens?.toSessionState(): SessionState = if (this == null) {
            SessionState.Unauthenticated
        } else {
            SessionState.Authenticated
        }
    }

    private companion object {
        const val errorEnvelope = "{\"success\":false,\"status\":401,\"message\":\"unauthorized\",\"data\":null}"
        const val refreshSuccessEnvelope = "{\"success\":true,\"status\":0,\"message\":\"ok\",\"data\":{\"accessToken\":\"new-access\",\"refreshToken\":\"new-refresh\"}}"
        const val emptyTokenEnvelope = "{\"success\":true,\"status\":0,\"message\":\"ok\",\"data\":{\"accessToken\":\"\",\"refreshToken\":\"new-refresh\"}}"
    }
}
