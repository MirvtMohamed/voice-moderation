package com.example.voice_moderation.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Define the DataStore extension property ONCE at file level
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "monitoring_prefs")

class MonitoringPreferencesRepository@Inject constructor(  @ApplicationContext private val context: Context) {

    companion object {
        private val isRecordingKey = booleanPreferencesKey("isRecording")
    }
   // private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "monitoring_prefs")
    val isRecording: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[isRecordingKey] ?: false }

    suspend fun setMonitoringState(isRecording: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isRecordingKey] = isRecording
        }
    }
}
