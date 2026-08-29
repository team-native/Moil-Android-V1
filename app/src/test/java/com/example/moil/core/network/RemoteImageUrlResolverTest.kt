package com.example.moil.core.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RemoteImageUrlResolverTest {
    @Test
    fun `서버 image path를 공개 images endpoint로 변환한다`() {
        assertEquals(
            "https://moil.team-native.kr/images/9f2a7c1b8e4d4f1a",
            RemoteImageUrlResolver.resolve("/image/9f2a7c1b8e4d4f1a"),
        )
    }

    @Test
    fun `알 수 없는 이미지 path는 URL로 만들지 않는다`() {
        assertNull(RemoteImageUrlResolver.resolve(null))
        assertNull(RemoteImageUrlResolver.resolve("/images/key"))
        assertNull(RemoteImageUrlResolver.resolve("/image/key/extra"))
        assertNull(RemoteImageUrlResolver.resolve("/image/../token"))
    }
}
