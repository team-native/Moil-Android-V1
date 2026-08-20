package com.example.moil.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.moil.core.network.SessionManager
import com.example.moil.core.network.SessionEvent
import com.example.moil.core.network.SessionState
import com.example.moil.feature.auth.presentation.LoginRoute
import com.example.moil.feature.auth.presentation.SignUpRoute
import com.example.moil.feature.auth.domain.CurrentUserProfileStore
import com.example.moil.core.model.GroupMemberRole
import kotlinx.serialization.Serializable

@Serializable
private sealed interface MoilAppDestination : NavKey {
    @Serializable
    data object Login : MoilAppDestination

    @Serializable
    data object SignUp : MoilAppDestination

    @Serializable
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
    val backStack = rememberNavBackStack(
        if (isAuthenticated) MoilAppDestination.Main else MoilAppDestination.Login,
    )
    var registeredEmail by remember { mutableStateOf("") }

    LaunchedEffect(isAuthenticated, sessionExpirationCount) {
        val expectedDestination = if (isAuthenticated) {
            MoilAppDestination.Main
        } else {
            MoilAppDestination.Login
        }
        if (backStack.lastOrNull() != expectedDestination) {
            backStack.clear()
            backStack.add(expectedDestination)
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<MoilAppDestination.Login> {
                LoginRoute(
                    initialEmail = registeredEmail,
                    onNavigateToSignUp = {
                        backStack.add(MoilAppDestination.SignUp)
                    },
                    onLoginCompleted = {
                        backStack.clear()
                        backStack.add(MoilAppDestination.Main)
                    },
                )
            }

            entry<MoilAppDestination.SignUp> {
                SignUpRoute(
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onSignUpCompleted = { email ->
                        registeredEmail = email
                        backStack.removeLastOrNull()
                    },
                )
            }

            entry<MoilAppDestination.Main> {
                MainTabRoute(
                    isDarkTheme = isDarkTheme,
                    onDarkThemeChanged = onDarkThemeChanged,
                    currentUserRole = currentUserRole,
                    onCurrentUserRoleChanged = onCurrentUserRoleChanged,
                )
            }
        },
    )
}
