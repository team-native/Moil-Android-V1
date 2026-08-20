package com.example.moil.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Main 탭 영역(하단 탭 + 그 위에 push되는 화면들)의 Navigation 3 백스택 키.
 * Calendar/Family/Profile은 각자 독립된 백스택을 갖는 최상위 탭이고,
 * GroupDetail/JoinGroup/CreateGroup/ProfileEdit는 그 탭의 백스택 위에 push되는 화면이다.
 */
@Serializable
sealed interface MainTabDestination : NavKey {
    @Serializable
    data object Calendar : MainTabDestination

    @Serializable
    data object Family : MainTabDestination

    @Serializable
    data object Profile : MainTabDestination

    @Serializable
    data class GroupDetail(val groupId: Long) : MainTabDestination

    @Serializable
    data object JoinGroup : MainTabDestination

    @Serializable
    data object CreateGroup : MainTabDestination

    @Serializable
    data object ProfileEdit : MainTabDestination
}
