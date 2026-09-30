package com.example.moil.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator

/**
 * 하단 탭(Calendar/Family/JoinGroup/Profile)마다 독립된 back stack을 유지한다.
 * 탭을 전환해도 그 탭에서 쌓아 둔 화면들이 그대로 보존된다.
 */
internal class MoilMainNavigationState(
    val startRoute: MoilMainDestination,
    private val topLevelRoutes: List<MoilMainDestination>,
    topLevelIndex: MutableState<Int>,
    val backStacks: Map<MoilMainDestination, NavBackStack<NavKey>>,
) {
    private var topLevelIndex: Int by topLevelIndex

    /** [MoilMainDestination]은 kotlinx.serialization 대상이라 rememberSaveable 기본 Saver로 못 담기 때문에 index로 저장한다. */
    var topLevelRoute: MoilMainDestination
        get() = topLevelRoutes[topLevelIndex]
        set(value) {
            val index = topLevelRoutes.indexOf(value)
            if (index >= 0) topLevelIndex = index
        }

    val currentBackStack: NavBackStack<NavKey>
        get() = backStacks.getValue(topLevelRoute)
}

@Composable
internal fun rememberMoilMainNavigationState(
    startRoute: MoilMainDestination,
    topLevelRoutes: List<MoilMainDestination>,
): MoilMainNavigationState {
    val topLevelIndex = rememberSaveable {
        mutableStateOf(topLevelRoutes.indexOf(startRoute).coerceAtLeast(0))
    }
    val backStacks = topLevelRoutes.associateWith { route -> rememberNavBackStack(route) }

    return remember(startRoute, topLevelRoutes) {
        MoilMainNavigationState(
            startRoute = startRoute,
            topLevelRoutes = topLevelRoutes,
            topLevelIndex = topLevelIndex,
            backStacks = backStacks,
        )
    }
}

/** 현재 활성 탭의 back stack을 [NavEntry] 목록으로 변환한다. 엔트리별 ViewModel/상태 저장 범위를 함께 부여한다. */
@Composable
internal fun MoilMainNavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): SnapshotStateList<NavEntry<NavKey>> {
    val viewModelStoreDecorator = rememberViewModelStoreNavEntryDecorator<NavKey>()
    val savedStateDecorator = rememberSaveableStateHolderNavEntryDecorator<NavKey>()

    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = listOf(savedStateDecorator, viewModelStoreDecorator),
            entryProvider = entryProvider,
        )
    }

    return decoratedEntries.getValue(topLevelRoute).toMutableStateList()
}

/** 탭 전환과 화면 push/pop을 담당한다. NavController를 대체한다. */
internal class MoilMainNavigator(private val state: MoilMainNavigationState) {

    /** 하단 탭 선택 시 호출한다. back stack 자체는 건드리지 않아 그 탭의 이전 기록이 보존된다. */
    fun navigateToTab(destination: MoilMainDestination) {
        if (destination in state.backStacks.keys) {
            state.topLevelRoute = destination
        }
    }

    /** 현재 탭의 back stack에 화면을 push한다. */
    fun push(destination: MoilMainDestination) {
        state.currentBackStack.add(destination)
    }

    /** [destination]이 이미 현재 탭 back stack에 있으면 중복 push하지 않는다. */
    fun pushIfAbsent(destination: MoilMainDestination) {
        if (!isOnCurrentBackStack(destination)) {
            state.currentBackStack.add(destination)
        }
    }

    /** 특정 탭으로 전환한 뒤 그 탭의 back stack에 화면을 얹는다. 뒤로가기가 목적지 → 탭 시작 화면 순서로 쌓이게 한다. */
    fun navigateToTabAndPush(tab: MoilMainDestination, destination: MoilMainDestination) {
        navigateToTab(tab)
        pushIfAbsent(destination)
    }

    /**
     * [destination]과 그 위에 쌓인 엔트리를 back stack에서 제거한다. 오버레이나 하위 화면을 코드로 닫을 때 쓴다.
     *
     * 화면을 띄운 탭과 닫으라는 신호가 오는 시점의 활성 탭이 다를 수 있어(예: 일정 저장 완료) 모든 탭을 함께 훑는다.
     * 탭의 시작 화면(index 0)은 제거하지 않아 back stack이 비는 일이 없다.
     */
    fun close(destination: MoilMainDestination) {
        state.backStacks.values.forEach { backStack ->
            val index = backStack.indexOf(destination)

            if (index > 0) {
                while (backStack.size > index) {
                    backStack.removeLastOrNull()
                }
            }
        }
    }

    fun isOnCurrentBackStack(destination: MoilMainDestination): Boolean =
        state.currentBackStack.contains(destination)

    fun isOnAnyBackStack(destination: MoilMainDestination): Boolean =
        state.backStacks.values.any { backStack -> backStack.contains(destination) }

    /**
     * 시스템/UI 뒤로가기: 현재 탭 back stack에서 pop하고, 탭의 시작 화면까지 pop되면 시작 탭으로 되돌린다.
     * 이미 시작 탭의 시작 화면이면 아무 것도 하지 않고 시스템에 위임한다(= 앱 종료).
     */
    fun goBack() {
        val backStack = state.currentBackStack

        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        } else if (state.topLevelRoute != state.startRoute) {
            state.topLevelRoute = state.startRoute
        }
    }
}
