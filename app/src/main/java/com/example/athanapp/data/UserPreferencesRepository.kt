package com.example.athanapp.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
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
    val isDarkMode: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_DARK_MODE] ?: true
        },
    val is12Hour: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_12_HOUR] ?: true
        },
    val isNotification: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_NOTIFICATION] ?: true
        },
    val latitude: Flow<Double> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[LATITUDE] ?: 0.0
        },
    val longitude: Flow<Double> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[LONGITUDE] ?: 0.0
        },
    val direction: Flow<Double> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[DIRECTION] ?: 0.0
        },
) {
    private companion object {
        val IS_START_SCREEN = booleanPreferencesKey("is_start_screen")
        val SELECTED_CITY = stringPreferencesKey("selected_city")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val IS_NOTIFICATION = booleanPreferencesKey("is_notification")
        val IS_12_HOUR = booleanPreferencesKey("is_12_hour")
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
        val DIRECTION = doublePreferencesKey("direction")
        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveLayoutPreference(isStartScreen: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_START_SCREEN] = isStartScreen
        }
    }

    suspend fun saveNotificationPreferences(isNotification: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_NOTIFICATION] = isNotification
        }
    }

    suspend fun saveSelectedCity(cityName: String?) {
        dataStore.edit { preferences ->
            preferences[SELECTED_CITY] = cityName ?: ""
        }
    }

    suspend fun saveTimePreferences(hour: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_12_HOUR] = hour
        }
    }

    suspend fun saveDarkModePreferences(mode: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = mode
        }
    }

    suspend fun saveCoordinates(coordinates: Pair<Double, Double>, direction: Double) {
        dataStore.edit { preferences ->
            preferences[LATITUDE] = coordinates.first
            preferences[LONGITUDE] = coordinates.second
            preferences[DIRECTION] = direction
        }
    }
}
