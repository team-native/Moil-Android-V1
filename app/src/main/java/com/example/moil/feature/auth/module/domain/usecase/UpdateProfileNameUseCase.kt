package com.example.moil.feature.auth.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.auth.module.domain.model.UserProfile
import com.example.moil.feature.auth.module.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateProfileNameUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    // 프로필 편집 화면의 저장 이벤트에서 사용자 이름을 서버에 반영합니다.
    // 성공 시 변경된 사용자 프로필을, 실패 시 공통 Domain 오류를 반환합니다.
    suspend operator fun invoke(name: String): MoilResult<UserProfile> = repository.updateProfileName(name)
}
