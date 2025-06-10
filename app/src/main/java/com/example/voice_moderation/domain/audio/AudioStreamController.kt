package com.example.voice_moderation.domain.audio


interface AudioStreamController {
    fun hasAudioPermission(): Boolean
    fun start()
    fun stop()
}
