package com.example.voice_moderation.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voice_moderation.data.Model.AudioFile
import com.example.voice_moderation.data.repository.AudioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioViewModel : ViewModel() {
    private val repository = AudioRepository()

    private val _audioFiles = MutableStateFlow<List<AudioFile>>(emptyList())
    val audioFiles = _audioFiles.asStateFlow()

    init {
        fetchAudioFiles()
    }

    private fun fetchAudioFiles() {
        viewModelScope.launch {
            try {
                _audioFiles.value = repository.getAudioFiles()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
