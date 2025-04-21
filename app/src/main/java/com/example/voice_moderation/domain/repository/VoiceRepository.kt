package com.example.voice_moderation.domain.repository


import com.example.voice_moderation.data.audio.AudioStreamController
import javax.inject.Inject

class VoiceRepository @Inject constructor(
    private val audioStreamController: AudioStreamController
) {
    fun startStreaming() {
        audioStreamController.start()
    }

    fun stopStreaming() {
        audioStreamController.stop()
    }

    fun hasPermission(): Boolean {
        return audioStreamController.hasAudioPermission()
    }
}
