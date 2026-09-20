package com.example.incometracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import com.example.incometracker.data.ThemePreferences
import com.example.incometracker.ui.theme.IncomeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            val themePreferences = remember { ThemePreferences(this) }
            val themeMode by themePreferences.themeMode.collectAsState(initial = ThemePreferences.ThemeMode.SYSTEM)
            val systemDark = isSystemInDarkTheme()
            
            val darkTheme = when(themeMode) {
                ThemePreferences.ThemeMode.LIGHT -> false
                ThemePreferences.ThemeMode.DARK -> true
                ThemePreferences.ThemeMode.SYSTEM -> systemDark
            }
            
            IncomeTheme(darkTheme = darkTheme) {
                IncomeApp()
            }
        }
    }
}
