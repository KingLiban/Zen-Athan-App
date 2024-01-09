package com.example.athanapp.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
    val isStartScreen: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
        preferences[IS_START_SCREEN] ?: true
        },
    val selectedCity: Flow<String> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[SELECTED_CITY] ?: ""
        },
) {
    private companion object {
        val IS_START_SCREEN = booleanPreferencesKey("is_start_screen")
        val SELECTED_CITY = stringPreferencesKey("selected_city")
        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveLayoutPreference(isStartScreen: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_START_SCREEN] = isStartScreen

        }
    }

    suspend fun saveSelectedCity(cityName: String?) {
        dataStore.edit { preferences ->
            preferences[SELECTED_CITY] = cityName ?: ""
        }
    }
}
