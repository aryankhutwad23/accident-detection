package com.example.myaccidentapplication.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    companion object {
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val USER_ID_KEY = intPreferencesKey("user_id")          // Int now
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token") // Added
        private val EMERGENCY_CONTACT_1_KEY = stringPreferencesKey("emergency_contact_1")
        private val EMERGENCY_CONTACT_2_KEY = stringPreferencesKey("emergency_contact_2")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN_KEY] ?: false }
    val userId: Flow<Int> = context.dataStore.data.map { it[USER_ID_KEY] ?: -1 }
    val userName: Flow<String> = context.dataStore.data.map { it[USER_NAME_KEY] ?: "" }
    val userEmail: Flow<String> = context.dataStore.data.map { it[USER_EMAIL_KEY] ?: "" }
    val userToken: Flow<String> = context.dataStore.data.map { it[USER_TOKEN_KEY] ?: "" }
    val emergencyContact1: Flow<String> = context.dataStore.data.map { it[EMERGENCY_CONTACT_1_KEY] ?: "" }
    val emergencyContact2: Flow<String> = context.dataStore.data.map { it[EMERGENCY_CONTACT_2_KEY] ?: "" }

    suspend fun saveUser(userId: Int, name: String, email: String, token: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = true
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = name
            preferences[USER_EMAIL_KEY] = email
            token?.let { preferences[USER_TOKEN_KEY] = it }
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = false
            preferences[USER_ID_KEY] = -1
            preferences[USER_NAME_KEY] = ""
            preferences[USER_EMAIL_KEY] = ""
            preferences[USER_TOKEN_KEY] = ""
            preferences[EMERGENCY_CONTACT_1_KEY] = ""
            preferences[EMERGENCY_CONTACT_2_KEY] = ""
        }
    }

    suspend fun saveEmergencyContacts(contact1: String, contact2: String) {
        context.dataStore.edit { preferences ->
            preferences[EMERGENCY_CONTACT_1_KEY] = contact1
            preferences[EMERGENCY_CONTACT_2_KEY] = contact2
        }
    }
}