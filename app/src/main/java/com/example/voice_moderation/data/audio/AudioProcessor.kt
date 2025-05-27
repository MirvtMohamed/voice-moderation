package com.example.voice_moderation.data.audio

import android.content.Context
import com.example.voice_moderation.data.model.domain.repository.VoiceRepository
import com.example.voice_moderation.data.remote.ApiResult
import com.example.voice_moderation.ui.alerts.AlertBroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AudioProcessor @Inject constructor(
    private val repository: VoiceRepository,
    private val context: Context
) {
    private val audioBuffer = ByteArrayOutputStream()
    private val sampleRate = 16000
    private val bytesPerSecond = sampleRate * 2  // 16-bit audio = 2 bytes per sample
    private val chunkDurationSeconds = 10
    private val chunkSizeBytes = bytesPerSecond * chunkDurationSeconds

    private val processingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mutex = Mutex() // For thread safety

    fun processAudioData(data: ByteArray) {
        processingScope.launch {
            mutex.withLock {
                audioBuffer.write(data, 0, data.size)

                if (audioBuffer.size() >= chunkSizeBytes) {
                    val audioChunk = audioBuffer.toByteArray()
                    audioBuffer.reset()

                    val tempFile = createTempWavFile(audioChunk)
                    analyzeAudioFile(tempFile)
                }
            }
        }
    }

    fun clear() {
        processingScope.launch {
            mutex.withLock {
                audioBuffer.reset()
            }
        }
    }

    private suspend fun analyzeAudioFile(audioFile: File) {
        try {
            repository.analyzeAudio(audioFile).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        val analysisResult = result.data
                        if (analysisResult.needsAlert) {
                            // Here you could emit an event or call a callback
                            AlertBroadcastReceiver.sendAlert(
                                context, // Inject this
                                analysisResult
                            )
//                            Timber.d("Alert needed: $analysisResult")
                        }
                    }
                    is ApiResult.Error -> {
                        Timber.e(result.exception, "Error analyzing audio")
                    }
                    is ApiResult.Loading -> {
                        // Handle loading state
                    }
                }
            }

            // Clean up temp file
            try {
                audioFile.delete()
            } catch (e: Exception) {
                Timber.e(e, "Error deleting temp file")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error processing audio chunk")
        }
    }

    private fun createTempWavFile(audioData: ByteArray): File {
        val tempFile = File.createTempFile("audio_chunk", ".wav")

        FileOutputStream(tempFile).use { out ->
            // WAV header
            writeString(out, "RIFF")
            writeInt(out, 36 + audioData.size)
            writeString(out, "WAVE")

            // Format subchunk
            writeString(out, "fmt ")
            writeInt(out, 16)
            writeShort(out, 1) // PCM
            writeShort(out, 1) // Mono
            writeInt(out, sampleRate)
            writeInt(out, sampleRate * 2) // Byte rate
            writeShort(out, 2) // Block align
            writeShort(out, 16) // Bits per sample

            // Data subchunk
            writeString(out, "data")
            writeInt(out, audioData.size)

            // Audio data
            out.write(audioData)
        }

        return tempFile
    }

    private fun writeString(out: FileOutputStream, value: String) {
        for (i in 0 until value.length) {
            out.write(value[i].code)
        }
    }

    private fun writeInt(out: FileOutputStream, value: Int) {
        out.write(value and 0xFF)
        out.write((value shr 8) and 0xFF)
        out.write((value shr 16) and 0xFF)
        out.write((value shr 24) and 0xFF)
    }

    private fun writeShort(out: FileOutputStream, value: Int) {
        out.write(value and 0xFF)
        out.write((value shr 8) and 0xFF)
    }
}