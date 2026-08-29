package com.example.moil.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.moil.core.model.GroupMemberRole
import com.example.moil.core.network.SessionEvent
import com.example.moil.core.network.SessionManager
import com.example.moil.core.network.SessionState
import com.example.moil.feature.auth.module.domain.model.SocialLoginCallback
import com.example.moil.feature.auth.module.domain.model.SocialLoginProvider
import com.example.moil.feature.auth.module.domain.repository.CurrentUserProfileStore
import com.example.moil.feature.auth.view.LoginRoute
import com.example.moil.feature.auth.view.SignUpRoute

private sealed interface MoilAppDestination {
    data object Login : MoilAppDestination
    data object SignUp : MoilAppDestination
    data object Main : MoilAppDestination
}

@Composable
fun MoilAppRoute(
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    currentUserRole: GroupMemberRole,
    onCurrentUserRoleChanged: (GroupMemberRole) -> Unit,
    sessionManager: SessionManager,
    currentUserProfileStore: CurrentUserProfileStore,
    deepLinkUri: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
) {
    val sessionState by sessionManager.sessionState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    var sessionExpirationCount by remember { mutableStateOf(0) }
    var pendingJoinGroupId by remember { mutableStateOf<Long?>(null) }
    var pendingOAuthCallback by remember { mutableStateOf<SocialLoginCallback?>(null) }
    var pendingOAuthFailure by remember { mutableStateOf(false) }
    val appDeepLink = remember(deepLinkUri) {
        AppDeepLinkParser.parse(deepLinkUri)
    }

    LaunchedEffect(appDeepLink) {
        when (val parsedDeepLink = appDeepLink) {
            is AppDeepLink.JoinGroup -> pendingJoinGroupId = parsedDeepLink.groupId
            is AppDeepLink.OAuthCallback -> {
                pendingOAuthCallback = SocialLoginCallback(
                    provider = SocialLoginProvider.fromWireValue(parsedDeepLink.provider)
                        ?: return@LaunchedEffect,
                    code = parsedDeepLink.code,
                    state = parsedDeepLink.state,
                    user = parsedDeepLink.user,
                )
            }
            is AppDeepLink.OAuthFailure -> pendingOAuthFailure = true
            null -> Unit
        }

        if (deepLinkUri != null) {
            onDeepLinkConsumed()
        }
    }

    LaunchedEffect(sessionState) {
        if (sessionState is SessionState.Unauthenticated) {
            currentUserProfileStore.clear()
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

    MoilAppNavigation(
        isDarkTheme = isDarkTheme,
        onDarkThemeChanged = onDarkThemeChanged,
        currentUserRole = currentUserRole,
        onCurrentUserRoleChanged = onCurrentUserRoleChanged,
        isAuthenticated = sessionState is SessionState.Authenticated,
        sessionExpirationCount = sessionExpirationCount,
        pendingJoinGroupId = pendingJoinGroupId,
        onJoinGroupDeepLinkHandled = { pendingJoinGroupId = null },
        pendingOAuthCallback = pendingOAuthCallback,
        onOAuthCallbackConsumed = { pendingOAuthCallback = null },
        hasPendingOAuthFailure = pendingOAuthFailure,
        onOAuthFailureConsumed = { pendingOAuthFailure = false },
    )
}

@Composable
private fun MoilAppNavigation(
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    currentUserRole: GroupMemberRole,
    onCurrentUserRoleChanged: (GroupMemberRole) -> Unit,
    isAuthenticated: Boolean,
    sessionExpirationCount: Int,
    pendingJoinGroupId: Long?,
    onJoinGroupDeepLinkHandled: () -> Unit,
    pendingOAuthCallback: SocialLoginCallback?,
    onOAuthCallbackConsumed: () -> Unit,
    hasPendingOAuthFailure: Boolean,
    onOAuthFailureConsumed: () -> Unit,
) {
    val destinationBackStack = remember {
        mutableStateListOf(if (isAuthenticated) MoilAppDestination.Main else MoilAppDestination.Login)
    }
    var registeredEmail by remember { mutableStateOf("") }
    val currentDestination = destinationBackStack.last()

    LaunchedEffect(isAuthenticated, sessionExpirationCount) {
        val expectedDestination = if (isAuthenticated) {
            MoilAppDestination.Main
        } else {
            MoilAppDestination.Login
        }
        if (destinationBackStack.last() != expectedDestination) {
            destinationBackStack.clear()
            destinationBackStack += expectedDestination
        }
    }

    BackHandler(enabled = destinationBackStack.size > 1) {
        destinationBackStack.removeAt(destinationBackStack.lastIndex)
    }

    when (currentDestination) {
        MoilAppDestination.Login -> {
            LoginRoute(
                initialEmail = registeredEmail,
                socialLoginCallback = pendingOAuthCallback,
                onSocialLoginCallbackConsumed = onOAuthCallbackConsumed,
                hasSocialLoginFailure = hasPendingOAuthFailure,
                onSocialLoginFailureConsumed = onOAuthFailureConsumed,
                onNavigateToSignUp = {
                    destinationBackStack += MoilAppDestination.SignUp
                },
                onLoginCompleted = {
                    destinationBackStack.clear()
                    destinationBackStack += MoilAppDestination.Main
                },
            )
        }

        MoilAppDestination.SignUp -> {
            SignUpRoute(
                onNavigateBack = {
                    destinationBackStack.removeAt(destinationBackStack.lastIndex)
                },
                onSignUpCompleted = { email ->
                    registeredEmail = email
                    destinationBackStack.removeAt(destinationBackStack.lastIndex)
                },
            )
        }

        MoilAppDestination.Main -> MainTabRoute(
            isDarkTheme = isDarkTheme,
            onDarkThemeChanged = onDarkThemeChanged,
            currentUserRole = currentUserRole,
            onCurrentUserRoleChanged = onCurrentUserRoleChanged,
            pendingJoinGroupId = pendingJoinGroupId,
            onJoinGroupDeepLinkHandled = onJoinGroupDeepLinkHandled,
        )
    }
}
