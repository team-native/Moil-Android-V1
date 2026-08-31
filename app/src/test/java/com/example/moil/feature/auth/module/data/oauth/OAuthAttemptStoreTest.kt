package com.example.moil.feature.auth.module.data.oauth

import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OAuthAttemptStoreTest {
    @Test
    fun `일치하지 않는 callback은 진행 중인 시도를 폐기하지 않는다`() {
        val attemptStore = OAuthAttemptStore()
        attemptStore.replace(OAuthAttempt(SocialLoginProvider.Google, "expected-state"))

        assertFalse(attemptStore.consume(SocialLoginProvider.Google, "unexpected-state"))
        assertTrue(attemptStore.consume(SocialLoginProvider.Google, "expected-state"))
        assertFalse(attemptStore.consume(SocialLoginProvider.Google, "expected-state"))
    }
}
