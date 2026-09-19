package com.example.incometracker.ui.lock

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.incometracker.data.SettingsStore
import com.example.incometracker.security.AppLockState
import com.example.incometracker.security.SecurePinStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UnlockState(val pin: String = "", val error: String? = null)

class UnlockViewModel(app: Application) : AndroidViewModel(app) {
    private val pinStore = SecurePinStore(app)
    private val settings = SettingsStore(app)

    val useBiometric = settings.useBiometric
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _state = MutableStateFlow(UnlockState())
    val state = _state.asStateFlow()

    fun setPin(v: String) {
        if (v.length <= 8 && v.all { it.isDigit() }) {
            _state.value = _state.value.copy(pin = v, error = null)
        }
    }

    fun unlockWithPin() {
        viewModelScope.launch {
            val ok = pinStore.verifyPin(_state.value.pin)
            if (ok) {
                _state.value = UnlockState()
                AppLockState.unlock()
            } else {
                _state.value = _state.value.copy(error = "Incorrect PIN")
            }
        }
    }

    fun unlockSuccess() {
        _state.value = UnlockState()
        AppLockState.unlock()
    }
}
