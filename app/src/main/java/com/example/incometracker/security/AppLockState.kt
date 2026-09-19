package com.example.incometracker.security

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object AppLockState {
    private val _locked = MutableStateFlow(false)
    val locked = _locked.asStateFlow()

    fun lock() { _locked.value = true }
    fun unlock() { _locked.value = false }
}
