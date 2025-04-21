package com.example.voice_moderation.data.audio


interface AudioStreamController {
    fun hasAudioPermission(): Boolean
    fun start()
    fun stop()
}
