package com.example.moil.feature.group.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.GroupSummary
import com.example.moil.feature.group.module.domain.repository.GroupRepository
import javax.inject.Inject

class JoinGroupUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    // 그룹 가입 프로필의 색상 또는 이미지 경로를 서버에 전달합니다.
    suspend operator fun invoke(
        code: String,
        nickname: String,
        color: GroupColor?,
        imagePath: String?,
    ): MoilResult<GroupSummary> = repository.joinGroup(code, nickname, color, imagePath)
}
