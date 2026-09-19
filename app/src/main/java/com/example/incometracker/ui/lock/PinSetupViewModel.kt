package com.example.incometracker.ui.lock

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.incometracker.data.SettingsStore
import com.example.incometracker.security.SecurePinStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PinSetupState(
    val hasExistingPin: Boolean = false,
    val currentPin: String = "",
    val newPin: String = "",
    val confirmPin: String = "",
    val error: String? = null
)

class PinSetupViewModel(app: Application) : AndroidViewModel(app) {
    private val pinStore = SecurePinStore(app)
    private val settings = SettingsStore(app)

    private val _state = MutableStateFlow(PinSetupState(hasExistingPin = pinStore.isPinSet()))
    val state = _state.asStateFlow()

    fun setCurrentPin(v: String) = updateDigits { it.copy(currentPin = v, error = null) }
    fun setNewPin(v: String) = updateDigits { it.copy(newPin = v, error = null) }
    fun setConfirmPin(v: String) = updateDigits { it.copy(confirmPin = v, error = null) }

    private fun updateDigits(block: (PinSetupState) -> PinSetupState) {
        val next = block(_state.value)
        fun ok(x: String) = x.length <= 8 && x.all { it.isDigit() }
        if (ok(next.currentPin) && ok(next.newPin) && ok(next.confirmPin)) _state.value = next
    }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            val s = _state.value
            if (s.hasExistingPin && !pinStore.verifyPin(s.currentPin)) {
                _state.value = s.copy(error = "Current PIN is incorrect")
                return@launch
            }
            if (s.newPin.length !in 4..8) {
                _state.value = s.copy(error = "PIN must be 4–8 digits")
                return@launch
            }
            if (s.newPin != s.confirmPin) {
                _state.value = s.copy(error = "PINs do not match")
                return@launch
            }

            pinStore.setPin(s.newPin)
            settings.setPinSet(true)
            onDone()
        }
    }

    fun clearPin(onDone: () -> Unit) {
        viewModelScope.launch {
            pinStore.clearPin()
            settings.setPinSet(false)
            onDone()
        }
    }
}
