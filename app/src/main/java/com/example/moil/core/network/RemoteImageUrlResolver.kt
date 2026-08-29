package com.example.moil.core.network

import com.example.moil.BuildConfig
import java.net.URI

/** 서버가 반환한 `/image/{key}`를 공개 이미지 조회 API URL로 변환합니다. */
object RemoteImageUrlResolver {
    private val imageKeyPattern = Regex("^[A-Za-z0-9._~-]+$")

    fun resolve(imagePath: String?): String? {
        val imageKey = imagePath
            ?.let(::runCatchingUri)
            ?.path
            ?.split('/')
            ?.filter(String::isNotBlank)
            ?.takeIf { segments -> segments.size == 2 && segments.first() == "image" }
            ?.last()
            ?.takeIf(imageKeyPattern::matches)
            ?: return null

        val baseUri = URI(BuildConfig.BASE_URL)
        return URI(
            baseUri.scheme,
            baseUri.authority,
            "/images/$imageKey",
            null,
        ).toString()
    }

    private fun runCatchingUri(value: String): URI? = runCatching { URI(value) }.getOrNull()
}
