package com.example.moil.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.example.moil.feature.auth.presentation.LoginRoute
import com.example.moil.feature.auth.presentation.SignUpRoute
import com.example.moil.core.model.GroupMemberRole

private sealed interface MoilAppDestination {
    data object Login : MoilAppDestination
    data object SignUp : MoilAppDestination
    data object Main : MoilAppDestination
}

@Composable
fun MoilAppNavigation(
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    currentUserRole: GroupMemberRole,
    onCurrentUserRoleChanged: (GroupMemberRole) -> Unit,
) {
    val destinationBackStack = remember {
        mutableStateListOf<MoilAppDestination>(MoilAppDestination.Login)
    }
    var registeredEmail by remember { mutableStateOf("") }
    val currentDestination = destinationBackStack.last()

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
