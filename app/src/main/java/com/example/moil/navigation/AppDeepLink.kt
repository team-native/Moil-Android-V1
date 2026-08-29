package com.example.moil.navigation

import java.net.URI

/** 현재 캘린더 브랜치에서 사용하는 그룹 가입 딥링크만 파싱합니다. */
sealed interface AppDeepLink {
    data class JoinGroup(val groupId: Long) : AppDeepLink
}

object AppDeepLinkParser {
    fun parse(deepLinkUri: String?): AppDeepLink? {
        val uri = deepLinkUri?.let(::runCatchingUri) ?: return null
        if (uri.scheme != "moil" || uri.host != "join") {
            return null
        }

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

    private fun runCatchingUri(value: String): URI? = runCatching { URI(value) }.getOrNull()
}
