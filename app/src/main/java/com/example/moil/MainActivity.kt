package com.example.moil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.example.moil.core.model.GroupMemberRole
import com.example.moil.core.settings.MemberRolePreferencesRepository
import com.example.moil.core.settings.ThemePreferencesRepository
import com.example.moil.navigation.MoilAppNavigation
import com.example.moil.ui.theme.MoilTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themePreferencesRepository = remember {
                ThemePreferencesRepository(applicationContext)
            }
            val memberRolePreferencesRepository = remember {
                MemberRolePreferencesRepository(applicationContext)
            }
            val isDarkThemeEnabled by themePreferencesRepository.isDarkTheme.collectAsState(
                initial = false,
            )
            val currentUserRole by memberRolePreferencesRepository.currentUserRole.collectAsState(
                initial = GroupMemberRole.Administrator,
            )
            val preferencesUpdateScope = rememberCoroutineScope()

            MoilTheme(darkTheme = isDarkThemeEnabled) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MoilAppNavigation(
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
                    )
                }
            }
        }
    }
}
