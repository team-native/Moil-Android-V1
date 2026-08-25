package com.example.moil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moil.core.model.GroupMemberRole
import com.example.moil.core.settings.MemberRolePreferencesRepository
import com.example.moil.core.settings.ThemePreferencesRepository
import com.example.moil.core.network.SessionManager
import com.example.moil.feature.auth.domain.CurrentUserProfileStore
import com.example.moil.navigation.MoilAppRoute
import com.example.moil.ui.theme.MoilTheme
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var currentUserProfileStore: CurrentUserProfileStore

    @Inject
    lateinit var themePreferencesRepository: ThemePreferencesRepository

    @Inject
    lateinit var memberRolePreferencesRepository: MemberRolePreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkThemeEnabled by themePreferencesRepository.isDarkTheme.collectAsStateWithLifecycle(
                initialValue = false,
            )
            val currentUserRole by memberRolePreferencesRepository.currentUserRole.collectAsStateWithLifecycle(
                initialValue = GroupMemberRole.Administrator,
            )
            val preferencesUpdateScope = rememberCoroutineScope()

            MoilTheme(darkTheme = isDarkThemeEnabled) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MoilAppRoute(
                        isDarkTheme = isDarkThemeEnabled,
                        onDarkThemeChanged = { isEnabled ->
                            preferencesUpdateScope.launch {
                                themePreferencesRepository.setDarkThemeEnabled(isEnabled)
                            }
                        },
                        currentUserRole = currentUserRole,
                        onCurrentUserRoleChanged = { role ->
                            preferencesUpdateScope.launch {
                                memberRolePreferencesRepository.setCurrentUserRole(role)
                            }
                        },
                        sessionManager = sessionManager,
                        currentUserProfileStore = currentUserProfileStore,
                    )
                }
            }
        }
    }
}
