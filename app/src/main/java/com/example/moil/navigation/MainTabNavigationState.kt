package com.example.moil.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack

/** 하단 탭으로 노출되는 최상위 목적지. 탭마다 독립된 백스택을 유지한다. */
private val mainTopLevelTabs = listOf(
    MainTabDestination.Calendar,
    MainTabDestination.Family,
    MainTabDestination.Profile,
)

/**
 * Main 탭 영역의 네비게이션 상태 홀더.
 * - 탭 전환([navigateToTab])은 현재 활성 탭만 바꾸고 각 탭의 백스택은 그대로 보존한다.
 * - 탭 위에 화면을 띄우는 것([push])은 현재 활성 탭의 백스택에 쌓인다.
 * - 시스템 뒤로가기([popOrNavigateToStart])는 활성 탭의 백스택을 pop하고,
 *   탭 루트까지 pop됐다면 시작 탭으로 돌아가며, 시작 탭의 루트에서는 아무것도 하지 않아
 *   시스템이 액티비티를 종료하도록 둔다.
 */
class MainTabNavigationState internal constructor(
    private val startTab: MainTabDestination,
    private val topLevelTabState: MutableState<MainTabDestination>,
    private val backStacks: Map<MainTabDestination, NavBackStack<NavKey>>,
) {
    val topLevelTab: MainTabDestination
        get() = topLevelTabState.value

    val currentBackStack: NavBackStack<NavKey>
        get() = backStacks.getValue(topLevelTab)

    val canGoBack: Boolean
        get() = currentBackStack.size > 1 || topLevelTab != startTab

    fun navigateToTab(tab: MainTabDestination) {
        topLevelTabState.value = tab
    }

    fun push(destination: MainTabDestination) {
        currentBackStack.add(destination)
    }

    fun pop() {
        currentBackStack.removeLastOrNull()
    }

    fun popOrNavigateToStart() {
        val stack = currentBackStack
        when {
            stack.size > 1 -> stack.removeLastOrNull()
            topLevelTab != startTab -> navigateToTab(startTab)
            else -> Unit
        }
    }

    /** 그룹 생성/가입 플로우가 끝나면 진입 지점(Calendar 또는 Profile)에 남은 화면을 정리하고 Calendar 탭으로 이동한다. */
    fun finishGroupOnboarding() {
        val stack = currentBackStack
        val top = stack.lastOrNull()
        if (top is MainTabDestination.CreateGroup || top is MainTabDestination.JoinGroup) {
            stack.removeLastOrNull()
        }
        navigateToTab(MainTabDestination.Calendar)
    }
}

@Composable
fun rememberMainTabNavigationState(
    startTab: MainTabDestination = MainTabDestination.Calendar,
): MainTabNavigationState {
    val topLevelTabState = remember { mutableStateOf(startTab) }
    val backStacks = mainTopLevelTabs.associateWith { tab -> rememberNavBackStack(tab) }
    return MainTabNavigationState(startTab, topLevelTabState, backStacks)
}
