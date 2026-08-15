package com.example.moil.feature.auth.domain

import kotlinx.coroutines.flow.StateFlow

/** 인증된 사용자의 표시 프로필을 인증 토큰과 분리해 보관하는 로컬 캐시 계약입니다. */
interface CurrentUserProfileStore {
    val profile: StateFlow<UserProfile?>

    fun save(profile: UserProfile)

    fun clear()
}
