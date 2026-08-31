package com.example.moil.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppDeepLinkParserTest {
    @Test
    fun `그룹 가입 링크에서 양의 groupId를 추출한다`() {
        assertEquals(
            AppDeepLink.JoinGroup(123L),
            AppDeepLinkParser.parse("moil://join/123"),
        )
    }

    @Test
    fun `OAuth callback에서 서버 토큰과 state를 추출한다`() {
        assertEquals(
            AppDeepLink.OAuthCallback(
                provider = "google",
                state = "state-1",
                accessToken = "access-token",
                refreshToken = "refresh-token",
            ),
            AppDeepLinkParser.parse(
                "moil://oauth/google/callback?accessToken=access-token&refreshToken=refresh-token&state=state-1",
            ),
        )
    }

    @Test
    fun `scheme과 필수 값이 없으면 링크를 무시한다`() {
        assertNull(AppDeepLinkParser.parse("https://example.com/join/123"))
        assertNull(AppDeepLinkParser.parse("moil://join/0"))
        assertNull(AppDeepLinkParser.parse("moil://oauth/google/callback?accessToken=access-token&refreshToken=refresh-token"))
        assertNull(AppDeepLinkParser.parse("moil://oauth/google/callback?accessToken=access-token&state=state-1"))
        assertEquals(
            AppDeepLink.OAuthFailure(provider = "google", state = "state-1"),
            AppDeepLinkParser.parse("moil://oauth/google/callback?error=access_denied&state=state-1"),
        )
    }
}
