package com.example.moil.navigation

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

sealed interface AppDeepLink {
    data class JoinGroup(val groupId: Long) : AppDeepLink

    data class OAuthCallback(
        val provider: String,
        val code: String,
        val state: String,
        val user: String?,
    ) : AppDeepLink

    data class OAuthFailure(
        val provider: String,
        val state: String?,
    ) : AppDeepLink
}

object AppDeepLinkParser {
    fun parse(deepLinkUri: String?): AppDeepLink? {
        val uri = deepLinkUri?.let(::runCatchingUri) ?: return null
        if (uri.scheme != "moil") {
            return null
        }

        return when (uri.host) {
            "join" -> parseJoinGroup(uri)
            "oauth" -> parseOAuthCallback(uri)
            else -> null
        }
    }

    private fun parseJoinGroup(uri: URI): AppDeepLink.JoinGroup? {
        val groupId = uri.path
            .split('/')
            .filter(String::isNotBlank)
            .takeIf { segments -> segments.size == 1 }
            ?.firstOrNull()
            ?.toLongOrNull()
            ?.takeIf { candidateId -> candidateId > 0L }
            ?: return null

        return AppDeepLink.JoinGroup(groupId)
    }

    private fun parseOAuthCallback(uri: URI): AppDeepLink? {
        val pathSegments = uri.path
            .split('/')
            .filter(String::isNotBlank)
        val provider = pathSegments
            .takeIf { segments -> segments.size == 2 && segments.last() == "callback" }
            ?.firstOrNull()
            ?.lowercase()
            ?.takeIf { candidateProvider -> candidateProvider in SUPPORTED_PROVIDERS }
            ?: return null
        val state = uri.queryParameter("state")?.takeIf(String::isNotBlank)
        if (uri.queryParameter("error") != null) {
            return AppDeepLink.OAuthFailure(provider = provider, state = state)
        }

        val code = uri.queryParameter("code")?.takeIf(String::isNotBlank) ?: return null
        val requiredState = state ?: return null

        return AppDeepLink.OAuthCallback(
            provider = provider,
            code = code,
            state = requiredState,
            user = uri.queryParameter("user"),
        )
    }

    private fun URI.queryParameter(name: String): String? = rawQuery
        ?.split('&')
        ?.firstOrNull { parameter -> parameter.substringBefore('=') == name }
        ?.substringAfter('=', missingDelimiterValue = "")
        ?.let { encodedValue -> URLDecoder.decode(encodedValue, StandardCharsets.UTF_8) }

    private fun runCatchingUri(value: String): URI? = runCatching { URI(value) }.getOrNull()

    private val SUPPORTED_PROVIDERS = setOf("google", "kakao", "apple")
}
