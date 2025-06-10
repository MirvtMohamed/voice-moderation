package com.example.voice_moderation.presentation.monitor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voice_moderation.data.preferences.MonitoringPreferencesRepository
import com.example.voice_moderation.service.VoiceMonitoringService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceMonitorViewModel @Inject constructor(
    private val monitoringPreferencesRepository: MonitoringPreferencesRepository
) : ViewModel() {

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    init {
        viewModelScope.launch {
            monitoringPreferencesRepository.isRecording.collect { state ->
                _isRecording.value = state
            }
        }
    }

    fun startMonitoring(context: Context) {
        viewModelScope.launch {
            // Set state first to prevent UI flicker
            monitoringPreferencesRepository.setMonitoringState(true)
            VoiceMonitoringService.startService(context)
        }
    }

    fun stopMonitoring(context: Context) {
        viewModelScope.launch {
            // Set state first to prevent UI flicker
            monitoringPreferencesRepository.setMonitoringState(false)
            VoiceMonitoringService.stopService(context)
        }
    }
}