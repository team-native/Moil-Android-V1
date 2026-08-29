package com.example.moil.feature.group.module.domain.usecase

import com.example.moil.core.domain.MoilResult
import com.example.moil.feature.group.module.domain.model.GroupColor
import com.example.moil.feature.group.module.domain.model.GroupMemberProfile
import com.example.moil.feature.group.module.domain.repository.GroupRepository
import javax.inject.Inject

class UpdateMyGroupProfileUseCase @Inject constructor(
    private val repository: GroupRepository,
) {
    // 그룹별 내 프로필 저장 이벤트에서 닉네임과 색상 또는 이미지 경로를 서버에 반영합니다.
    suspend operator fun invoke(
        groupId: Long,
        nickname: String,
        color: GroupColor?,
        imagePath: String?,
    ): MoilResult<GroupMemberProfile> = repository.updateMyGroupProfile(groupId, nickname, color, imagePath)
}
