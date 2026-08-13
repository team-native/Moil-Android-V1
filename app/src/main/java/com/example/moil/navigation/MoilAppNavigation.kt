package com.example.moil.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.moil.core.network.SessionManager
import com.example.moil.core.network.SessionEvent
import com.example.moil.core.network.SessionState
import com.example.moil.feature.auth.presentation.LoginRoute
import com.example.moil.feature.auth.presentation.SignUpRoute
import com.example.moil.feature.auth.domain.CurrentUserProfileStore
import com.example.moil.core.model.GroupMemberRole

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
) {
    val sessionState by sessionManager.sessionState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    var sessionExpirationCount by remember { mutableStateOf(0) }

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
        )
    }
}
