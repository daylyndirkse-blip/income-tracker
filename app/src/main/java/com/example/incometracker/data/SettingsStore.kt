package com.example.incometracker.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.incometracker.util.WeekStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsStore(private val context: Context) {

    private val CURRENCY = stringPreferencesKey("currency_code")
    private val WEEK_START = stringPreferencesKey("week_start")

    private val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
    private val USE_BIOMETRIC = booleanPreferencesKey("use_biometric")
    private val PIN_SET = booleanPreferencesKey("pin_set")
    private val LAST_BACKGROUND_AT = longPreferencesKey("last_background_at")

    val currencyCode: Flow<String> = context.dataStore.data.map { it[CURRENCY] ?: "USD" }

    val weekStart: Flow<WeekStart> = context.dataStore.data.map {
        runCatching { WeekStart.valueOf(it[WEEK_START] ?: WeekStart.MONDAY.name) }
            .getOrDefault(WeekStart.MONDAY)
    }

    val appLockEnabled: Flow<Boolean> = context.dataStore.data.map { it[APP_LOCK_ENABLED] ?: false }
    val useBiometric: Flow<Boolean> = context.dataStore.data.map { it[USE_BIOMETRIC] ?: false }
    val pinSet: Flow<Boolean> = context.dataStore.data.map { it[PIN_SET] ?: false }
    val lastBackgroundAt: Flow<Long> = context.dataStore.data.map { it[LAST_BACKGROUND_AT] ?: 0L }

    suspend fun setCurrency(code: String) { context.dataStore.edit { it[CURRENCY] = code } }
    suspend fun setWeekStart(value: WeekStart) { context.dataStore.edit { it[WEEK_START] = value.name } }

    suspend fun setAppLockEnabled(enabled: Boolean) { context.dataStore.edit { it[APP_LOCK_ENABLED] = enabled } }
    suspend fun setUseBiometric(enabled: Boolean) { context.dataStore.edit { it[USE_BIOMETRIC] = enabled } }
    suspend fun setPinSet(set: Boolean) { context.dataStore.edit { it[PIN_SET] = set } }

    suspend fun setLastBackgroundAt(epochMillis: Long) {
        context.dataStore.edit { it[LAST_BACKGROUND_AT] = epochMillis }
    }
}
