package com.appclimb.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val TOKEN_KEY = stringPreferencesKey("access_token")
        val USER_ID_KEY = longPreferencesKey("user_id")
        val NICKNAME_KEY = stringPreferencesKey("nickname")
        val ROLE_KEY = stringPreferencesKey("role")
    }

    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val userId: Flow<Long?> = context.dataStore.data.map { it[USER_ID_KEY] }
    val nickname: Flow<String?> = context.dataStore.data.map { it[NICKNAME_KEY] }
    val role: Flow<String?> = context.dataStore.data.map { it[ROLE_KEY] }

    suspend fun saveAuth(token: String, userId: Long, nickname: String, role: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
            prefs[NICKNAME_KEY] = nickname
            prefs[ROLE_KEY] = role
        }
    }

    suspend fun clearAuth() {
        context.dataStore.edit { it.clear() }
    }
}
