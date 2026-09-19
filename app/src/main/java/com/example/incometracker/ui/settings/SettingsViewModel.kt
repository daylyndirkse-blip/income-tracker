package com.example.incometracker.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.incometracker.data.SettingsStore
import com.example.incometracker.security.AppLockState
import com.example.incometracker.util.WeekStart
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val store = SettingsStore(app)

    val currency = store.currencyCode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "USD")
    val weekStart = store.weekStart.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeekStart.MONDAY)

    val lockEnabled = store.appLockEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val useBiometric = store.useBiometric.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val pinSet = store.pinSet.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun setCurrency(code: String) = viewModelScope.launch { store.setCurrency(code) }
    fun setWeekStart(ws: WeekStart) = viewModelScope.launch { store.setWeekStart(ws) }

    fun setLockEnabled(v: Boolean) = viewModelScope.launch {
        store.setAppLockEnabled(v)
        if (!v) AppLockState.unlock()
    }

    fun setUseBiometric(v: Boolean) = viewModelScope.launch { store.setUseBiometric(v) }
}
