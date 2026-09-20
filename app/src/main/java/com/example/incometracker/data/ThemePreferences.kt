package com.example.incometracker.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "theme_preferences")

class ThemePreferences(private val context: Context) {
    private val THEME_MODE = stringPreferencesKey("theme_mode")
    
    enum class ThemeMode {
        LIGHT, DARK, SYSTEM
    }
    
    val themeMode: Flow<ThemeMode> = context.themeDataStore.data.map { preferences ->
        when (preferences[THEME_MODE]) {
            "DARK" -> ThemeMode.DARK
            "LIGHT" -> ThemeMode.LIGHT
            else -> ThemeMode.SYSTEM
        }
    }
    
    suspend fun setThemeMode(mode: ThemeMode) {
        context.themeDataStore.edit { preferences ->
            preferences[THEME_MODE] = mode.name
        }
    }
}
