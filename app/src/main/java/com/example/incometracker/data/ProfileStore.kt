package com.example.incometracker.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.profileDataStore by preferencesDataStore(name = "profile")

class ProfileStore(private val context: Context) {
    private val NAME = stringPreferencesKey("user_name")
    private val AVATAR_COLOR = stringPreferencesKey("avatar_color")

    val userName: Flow<String> = context.profileDataStore.data.map { it[NAME] ?: "" }
    val avatarColor: Flow<String> = context.profileDataStore.data.map { it[AVATAR_COLOR] ?: "7C3AED" }

    suspend fun setName(name: String) { context.profileDataStore.edit { it[NAME] = name } }
    suspend fun setAvatarColor(hex: String) { context.profileDataStore.edit { it[AVATAR_COLOR] = hex } }
}
