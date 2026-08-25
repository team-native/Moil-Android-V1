package com.example.moil.core.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val themePreferencesDataStoreName = "theme_preferences"

private val Context.themePreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = themePreferencesDataStoreName,
)

class ThemePreferencesRepository @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val appContext = context.applicationContext

    val isDarkTheme: Flow<Boolean> = appContext.themePreferencesDataStore.data.map { preferences ->
        preferences[isDarkThemeKey] ?: false
    }

    suspend fun setDarkThemeEnabled(isEnabled: Boolean) {
        appContext.themePreferencesDataStore.edit { preferences ->
            preferences[isDarkThemeKey] = isEnabled
        }
    }

    private companion object {
        val isDarkThemeKey = booleanPreferencesKey("is_dark_theme")
    }
}
