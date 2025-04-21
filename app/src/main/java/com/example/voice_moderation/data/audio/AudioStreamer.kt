package com.example.voice_moderation.data.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import com.example.voice_moderation.data.network.WebSocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class AudioStreamer(
    private val context: Context,
    private val webSocketClient: WebSocketClient
) : AudioStreamController {
    private var audioRecord: AudioRecord? = null
    private var isStreaming = false
    private val sampleRate = 16000
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    override fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun start() {
        if (!hasAudioPermission()) {
            Timber.tag("AudioStreamer").e("Microphone permission not granted.")
            return
        }

        if (isStreaming) return

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
            audioRecord?.startRecording()
            isStreaming = true

            CoroutineScope(Dispatchers.IO).launch {
                val buffer = ByteArray(bufferSize)
                while (isStreaming) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0 && isSpeech(buffer)) {
                        webSocketClient.send(buffer.copyOf(read))
                    }
                }
            }

            webSocketClient.connect()
        } catch (e: SecurityException) {
            Timber.tag("AudioStreamer").e(e, "SecurityException when starting recording.")
        }
    }


    override fun stop() {
        isStreaming = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        webSocketClient.disconnect()
    }

    // Very basic VAD
    private fun isSpeech(audioData: ByteArray): Boolean {
        val energy = audioData.sumOf { it.toInt() * it.toInt() } / audioData.size
        return energy > 1000
    }
}
