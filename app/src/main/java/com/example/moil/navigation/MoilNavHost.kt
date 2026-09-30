package com.example.moil.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.example.moil.core.component.MoilBottomSheetStyle
import com.example.moil.core.component.applyDialogBackdropBlur
import com.example.moil.core.network.SessionEvent
import com.example.moil.core.network.SessionManager
import com.example.moil.core.network.SessionState
import com.example.moil.feature.auth.module.domain.model.SocialLoginCallback
import com.example.moil.feature.auth.module.domain.model.SocialLoginFailure
import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider
import com.example.moil.feature.auth.module.domain.repository.CurrentUserProfileStore
import com.example.moil.feature.auth.view.LoginRoute
import com.example.moil.feature.auth.view.SignUpRoute
import com.example.moil.feature.calendar.view.CalendarScreen
import com.example.moil.feature.calendar.view.ScheduleDatePickerDialog
import com.example.moil.feature.calendar.view.ScheduleDeleteConfirmationDialog
import com.example.moil.feature.calendar.view.ScheduleLocationDialog
import com.example.moil.feature.calendar.view.ScheduleMemoDialog
import com.example.moil.feature.calendar.view.ScheduleTimePickerDialog
import com.example.moil.feature.calendar.viewmodel.CalendarScreenEvent
import com.example.moil.feature.calendar.viewmodel.CalendarViewModel
import com.example.moil.feature.group.viewmodel.GroupViewModel
import com.example.moil.feature.group.viewmodel.JoinGroupUiState
import com.example.moil.navigation.route.AdministratorTransferRoute
import com.example.moil.navigation.route.CreateGroupRoute
import com.example.moil.navigation.route.FamilyTabRoute
import com.example.moil.navigation.route.GroupDetailRoute
import com.example.moil.navigation.route.GroupRenameRoute
import com.example.moil.navigation.route.InviteShareRoute
import com.example.moil.navigation.route.JoinGroupTabRoute
import com.example.moil.navigation.route.MemberPermissionsRoute
import com.example.moil.navigation.route.ProfileEditRoute
import com.example.moil.navigation.route.ProfileTabRoute
import com.example.moil.navigation.route.ScheduleSheetRoute
import com.example.moil.ui.theme.MoilTimePickerDimension
import java.time.LocalTime

/** 인증 전·후 탐색 그래프를 세션 상태에 따라 하나만 구성하는 앱 전체 진입점이다. */
@Composable
fun MoilNavHost(
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    sessionManager: SessionManager,
    currentUserProfileStore: CurrentUserProfileStore,
    deepLinkUri: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
) {
    val sessionState by sessionManager.sessionState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    var sessionExpirationCount by rememberSaveable { mutableIntStateOf(0) }
    var registeredEmail by rememberSaveable { mutableStateOf("") }
    var pendingJoinGroupId by remember { mutableStateOf<Long?>(null) }
    var pendingSocialLoginCallback by remember { mutableStateOf<SocialLoginCallback?>(null) }
    var pendingSocialLoginFailure by remember { mutableStateOf<SocialLoginFailure?>(null) }
    val appDeepLink = remember(deepLinkUri) { AppDeepLinkParser.parse(deepLinkUri) }

    LaunchedEffect(appDeepLink) {
        when (val parsedDeepLink = appDeepLink) {
            is AppDeepLink.JoinGroup -> pendingJoinGroupId = parsedDeepLink.groupId

            is AppDeepLink.OAuthCallback -> {
                pendingSocialLoginCallback = SocialLoginCallback(
                    provider = SocialLoginProvider.fromWireValue(parsedDeepLink.provider)
                        ?: return@LaunchedEffect,
                    state = parsedDeepLink.state,
                    accessToken = parsedDeepLink.accessToken,
                    refreshToken = parsedDeepLink.refreshToken,
                )
            }

            is AppDeepLink.OAuthFailure -> {
                pendingSocialLoginFailure = SocialLoginFailure(
                    provider = SocialLoginProvider.fromWireValue(parsedDeepLink.provider)
                        ?: return@LaunchedEffect,
                    state = parsedDeepLink.state,
                )
            }

            null -> Unit
        }

        if (deepLinkUri != null) {
            onDeepLinkConsumed()
        }
    }

    // SessionManager는 상태(sessionState)와 만료 이벤트(sessionEvents)를 별도 스트림으로 노출한다.
    // 이미 Unauthenticated인 상태에서 다시 401이 오면 sessionState 값은 그대로라 재구성이 일어나지 않으므로,
    // 만료 횟수를 함께 키로 삼아 "어느 화면에 있든 로그인 화면으로 되돌리고 프로필 캐시를 비운다"가 매번 실행되게 한다.
    LaunchedEffect(sessionState, sessionExpirationCount) {
        if (sessionState is SessionState.Unauthenticated) {
            currentUserProfileStore.clear()
        }
    }

    LaunchedEffect(sessionState) {
        if (sessionState is SessionState.Authenticated) {
            // 로그인에 성공하고도 남아 있는 소셜 로그인 pending 값이 다음 로그인 화면에서 다시 소비되지 않게 비운다.
            pendingSocialLoginCallback = null
            pendingSocialLoginFailure = null
        }
    }

    LaunchedEffect(sessionManager, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            sessionManager.sessionEvents.collect { event ->
                if (event is SessionEvent.Expired) {
                    sessionExpirationCount += 1
                }
            }
        }
    }

    key(sessionState, sessionExpirationCount) {
        if (sessionState is SessionState.Authenticated) {
            MoilMainNavDisplay(
                isDarkTheme = isDarkTheme,
                onDarkThemeChanged = onDarkThemeChanged,
                pendingJoinGroupId = pendingJoinGroupId,
                onJoinGroupDeepLinkHandled = { pendingJoinGroupId = null },
            )
        } else {
            MoilAuthNavDisplay(
                initialEmail = registeredEmail,
                socialLoginCallback = pendingSocialLoginCallback,
                onSocialLoginCallbackConsumed = { pendingSocialLoginCallback = null },
                socialLoginFailure = pendingSocialLoginFailure,
                onSocialLoginFailureConsumed = { pendingSocialLoginFailure = null },
                onSignUpCompleted = { email -> registeredEmail = email },
            )
        }
    }
}

/** 로그인 전 선형 플로우(로그인 → 회원가입)를 담당한다. */
@Composable
private fun MoilAuthNavDisplay(
    initialEmail: String,
    socialLoginCallback: SocialLoginCallback?,
    onSocialLoginCallbackConsumed: () -> Unit,
    socialLoginFailure: SocialLoginFailure?,
    onSocialLoginFailureConsumed: () -> Unit,
    onSignUpCompleted: (String) -> Unit,
) {
    val backStack = rememberNavBackStack(MoilAuthDestination.Login)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        entryProvider = entryProvider {
            entry<MoilAuthDestination.Login> {
                LoginRoute(
                    initialEmail = initialEmail,
                    socialLoginCallback = socialLoginCallback,
                    onSocialLoginCallbackConsumed = onSocialLoginCallbackConsumed,
                    socialLoginFailure = socialLoginFailure,
                    onSocialLoginFailureConsumed = onSocialLoginFailureConsumed,
                    onNavigateToSignUp = { backStack.add(MoilAuthDestination.SignUp) },
                    // 로그인 성공은 sessionState 변화 + key(sessionState)가 이미 처리하므로 별도 back stack 조작이 필요 없다.
                    onLoginCompleted = {},
                )
            }

            entry<MoilAuthDestination.SignUp> {
                SignUpRoute(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onSignUpCompleted = { email ->
                        onSignUpCompleted(email)
                        backStack.removeLastOrNull()
                    },
                )
            }
        },
    )
}

/** 로그인 후 하단 탭 4개와 그 하위 화면·오버레이를 담당한다. */
@Composable
private fun MoilMainNavDisplay(
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    pendingJoinGroupId: Long?,
    onJoinGroupDeepLinkHandled: () -> Unit,
) {
    val mainFlowViewModelStoreOwner = rememberMoilMainFlowViewModelStoreOwner()
    val mainTabViewModel: MainTabViewModel =
        hiltViewModel(viewModelStoreOwner = mainFlowViewModelStoreOwner)
    val groupViewModel: GroupViewModel =
        hiltViewModel(viewModelStoreOwner = mainFlowViewModelStoreOwner)
    val calendarViewModel: CalendarViewModel =
        hiltViewModel(viewModelStoreOwner = mainFlowViewModelStoreOwner)

    val mainUiState = rememberMoilMainUiState(isDarkTheme)
    val navigationState = rememberMoilMainNavigationState(
        startRoute = MoilMainDestination.Calendar,
        topLevelRoutes = moilTabDestinations,
    )
    val navigator = remember(navigationState) { MoilMainNavigator(navigationState) }
    val calendarScreenEventHandler = remember(
        mainUiState,
        groupViewModel,
        calendarViewModel,
        navigator,
    ) {
        CalendarScreenEventHandler(
            mainUiState = mainUiState,
            groupViewModel = groupViewModel,
            calendarViewModel = calendarViewModel,
            navigator = navigator,
        )
    }
    val sceneStrategies = remember {
        listOf(
            MoilDialogSceneStrategy(),
            MoilBottomSheetSceneStrategy(),
            SinglePaneSceneStrategy(),
        )
    }

    MoilMainUiStateEffects(
        mainUiState = mainUiState,
        isDarkTheme = isDarkTheme,
        mainTabViewModel = mainTabViewModel,
        groupViewModel = groupViewModel,
        calendarViewModel = calendarViewModel,
        navigationState = navigationState,
        navigator = navigator,
    )

    // Navigation 3는 딥링크를 공식 지원하지 않으므로, 이미 구성된 NavDisplay에 지연 push로 반영한다.
    // 탭 전환을 먼저 해야 뒤로가기가 그 탭의 시작 화면으로 자연스럽게 이어진다.
    LaunchedEffect(pendingJoinGroupId) {
        pendingJoinGroupId?.let { groupId ->
            mainUiState.joinGroupUiState = JoinGroupUiState(pendingGroupId = groupId)
            navigator.navigateToTab(MoilMainDestination.JoinGroup)
            onJoinGroupDeepLinkHandled()
        }
    }

    NavDisplay(
        entries = navigationState.toEntries(
            entryProvider {
                entry<MoilMainDestination.Calendar> {
                    // 시간 선택 다이얼로그는 창 배경 블러가 막힌 기기를 대비해 달력 자체도 흐리게 만든다.
                    val isTimePickerOpen = navigator
                        .isOnAnyBackStack(MoilMainDestination.ScheduleTimePicker)

                    CalendarScreen(
                        uiState = mainUiState.calendarUiState,
                        onEvent = calendarScreenEventHandler::handle,
                        modifier = Modifier.applyDialogBackdropBlur(
                            shouldBlur = isTimePickerOpen,
                            blurRadius = MoilTimePickerDimension.BackgroundBlur,
                        ),
                    )
                }

                entry<MoilMainDestination.ScheduleSheet>(
                    metadata = MoilBottomSheetSceneStrategy.bottomSheet(MoilBottomSheetStyle.Schedule),
                ) {
                    ScheduleSheetRoute(
                        mainUiState = mainUiState,
                        groupViewModel = groupViewModel,
                        calendarViewModel = calendarViewModel,
                        onEvent = calendarScreenEventHandler::handle,
                    )
                }

                entry<MoilMainDestination.ScheduleDatePicker>(
                    metadata = MoilDialogSceneStrategy.dialog(),
                ) {
                    ScheduleDatePickerDialog(
                        selectedDate = mainUiState.calendarUiState.scheduleDate,
                        onDateConfirmed = { selectedDate ->
                            calendarScreenEventHandler
                                .handle(CalendarScreenEvent.ScheduleDateChanged(selectedDate))
                            navigator.goBack()
                        },
                        onDismiss = navigator::goBack,
                    )
                }

                entry<MoilMainDestination.ScheduleTimePicker>(
                    metadata = MoilDialogSceneStrategy.dialog(),
                ) {
                    ScheduleTimePickerDialog(
                        selectedTime = mainUiState.calendarUiState.scheduleStartTime
                            ?: DEFAULT_SCHEDULE_START_TIME,
                        onTimeConfirmed = { selectedTime ->
                            calendarScreenEventHandler
                                .handle(CalendarScreenEvent.ScheduleTimeChanged(selectedTime))
                            navigator.goBack()
                        },
                        onDismiss = navigator::goBack,
                    )
                }

                entry<MoilMainDestination.ScheduleLocation>(
                    metadata = MoilDialogSceneStrategy.dialog(),
                ) {
                    ScheduleLocationDialog(
                        initialLocation = mainUiState.calendarUiState.scheduleLocation,
                        onLocationConfirmed = { location ->
                            calendarScreenEventHandler
                                .handle(CalendarScreenEvent.ScheduleLocationChanged(location))
                            navigator.goBack()
                        },
                        onDismiss = navigator::goBack,
                    )
                }

                entry<MoilMainDestination.ScheduleMemo>(
                    metadata = MoilDialogSceneStrategy.dialog(),
                ) {
                    ScheduleMemoDialog(
                        initialMemo = mainUiState.calendarUiState.scheduleMemo,
                        onMemoConfirmed = { memo ->
                            calendarScreenEventHandler
                                .handle(CalendarScreenEvent.ScheduleMemoChanged(memo))
                            navigator.goBack()
                        },
                        onDismiss = navigator::goBack,
                    )
                }

                entry<MoilMainDestination.ScheduleDeleteConfirmation>(
                    metadata = MoilDialogSceneStrategy.dialog(),
                ) {
                    ScheduleDeleteConfirmationDialog(
                        onConfirm = {
                            calendarScreenEventHandler
                                .handle(CalendarScreenEvent.ScheduleDeleteConfirmed)
                        },
                        onDismiss = navigator::goBack,
                    )
                }

                entry<MoilMainDestination.Family> {
                    FamilyTabRoute(
                        mainUiState = mainUiState,
                        groupViewModel = groupViewModel,
                        navigator = navigator,
                    )
                }

                entry<MoilMainDestination.GroupDetail> { key ->
                    GroupDetailRoute(
                        groupId = key.groupId,
                        mainUiState = mainUiState,
                        groupViewModel = groupViewModel,
                        navigator = navigator,
                    )
                }

                entry<MoilMainDestination.GroupRename>(
                    metadata = MoilDialogSceneStrategy.dialog(),
                ) {
                    GroupRenameRoute(
                        mainUiState = mainUiState,
                        groupViewModel = groupViewModel,
                        navigator = navigator,
                    )
                }

                entry<MoilMainDestination.MemberPermissions>(
                    metadata = MoilBottomSheetSceneStrategy.bottomSheet(MoilBottomSheetStyle.Overlay),
                ) {
                    MemberPermissionsRoute(
                        mainUiState = mainUiState,
                        navigator = navigator,
                    )
                }

                entry<MoilMainDestination.InviteShare>(
                    metadata = MoilBottomSheetSceneStrategy.bottomSheet(MoilBottomSheetStyle.Overlay),
                ) {
                    InviteShareRoute(
                        mainUiState = mainUiState,
                        navigator = navigator,
                    )
                }

                entry<MoilMainDestination.AdministratorTransfer>(
                    metadata = MoilDialogSceneStrategy.dialog(),
                ) {
                    AdministratorTransferRoute(
                        mainUiState = mainUiState,
                        groupViewModel = groupViewModel,
                        navigator = navigator,
                    )
                }

                entry<MoilMainDestination.JoinGroup> {
                    JoinGroupTabRoute(
                        mainUiState = mainUiState,
                        groupViewModel = groupViewModel,
                        navigator = navigator,
                    )
                }

                entry<MoilMainDestination.CreateGroup> {
                    CreateGroupRoute(
                        mainUiState = mainUiState,
                        groupViewModel = groupViewModel,
                        navigator = navigator,
                    )
                }

                entry<MoilMainDestination.Profile> {
                    ProfileTabRoute(
                        mainUiState = mainUiState,
                        mainTabViewModel = mainTabViewModel,
                        navigator = navigator,
                        onDarkThemeChanged = onDarkThemeChanged,
                    )
                }

                entry<MoilMainDestination.ProfileEdit> {
                    ProfileEditRoute(
                        mainUiState = mainUiState,
                        mainTabViewModel = mainTabViewModel,
                        navigator = navigator,
                    )
                }
            },
        ),
        onBack = navigator::goBack,
        sceneStrategies = sceneStrategies,
        transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
    )
}

/**
 * 메인 플로우 전체가 공유하는 ViewModel 스코프를 만든다.
 *
 * 탭·하위 화면·오버레이가 같은 `GroupViewModel`/`CalendarViewModel`/`MainTabViewModel` 인스턴스를 봐야 하므로
 * 개별 NavEntry가 아니라 이 Composable 수명에 묶고, 로그아웃 등으로 메인 플로우를 벗어나면 비워
 * 다음 로그인에 이전 계정 데이터가 남지 않게 한다.
 */
@Composable
private fun rememberMoilMainFlowViewModelStoreOwner(): ViewModelStoreOwner {
    val parentViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
    val mainFlowViewModelStoreOwner = remember(parentViewModelStoreOwner) {
        MoilMainFlowViewModelStoreOwner(parentViewModelStoreOwner)
    }

    DisposableEffect(mainFlowViewModelStoreOwner) {
        onDispose { mainFlowViewModelStoreOwner.viewModelStore.clear() }
    }

    return mainFlowViewModelStoreOwner
}

/** Hilt ViewModel을 만들 수 있도록 상위 owner의 기본 factory를 물려받는 독립 ViewModel 스코프다. */
private class MoilMainFlowViewModelStoreOwner(
    parentViewModelStoreOwner: ViewModelStoreOwner,
) : ViewModelStoreOwner, HasDefaultViewModelProviderFactory {
    override val viewModelStore: ViewModelStore = ViewModelStore()

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory =
        (parentViewModelStoreOwner as? HasDefaultViewModelProviderFactory)
            ?.defaultViewModelProviderFactory
            ?: ViewModelProvider.NewInstanceFactory()

    override val defaultViewModelCreationExtras: CreationExtras =
        (parentViewModelStoreOwner as? HasDefaultViewModelProviderFactory)
            ?.defaultViewModelCreationExtras
            ?: CreationExtras.Empty
}

private val DEFAULT_SCHEDULE_START_TIME: LocalTime = LocalTime.of(9, 0)
