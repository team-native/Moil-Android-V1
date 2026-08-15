package com.example.moil.core.network

import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = sessionManager.currentTokens()?.accessToken
        val request = if (accessToken.isNullOrBlank()) {
            chain.request()
        } else {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        }

        return chain.proceed(request)
    }
}
