package com.example.incometracker.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.incometracker.data.ProfileStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(app: Application) : AndroidViewModel(app) {
    private val store = ProfileStore(app)

    val userName = store.userName.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")
    val avatarColor = store.avatarColor.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "7C3AED")

    fun setName(name: String) = viewModelScope.launch { store.setName(name) }
}
