package com.example.voice_moderation.presentation.monitor

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.voice_moderation.service.VoiceMonitoringService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VoiceMonitorViewModel @Inject constructor() : ViewModel() {

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    fun startMonitoring(context: Context) {
        if (!_isRecording.value) {
            VoiceMonitoringService.startService(context)
            _isRecording.value = true
        }
    }

    fun stopMonitoring(context: Context) {
        if (_isRecording.value) {
            VoiceMonitoringService.stopService(context)
            _isRecording.value = false
        }
    }

    override fun onCleared() {
        _isRecording.value = false
        super.onCleared()
    }
}
