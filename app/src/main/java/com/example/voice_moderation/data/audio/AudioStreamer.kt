package com.example.voice_moderation.data.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import com.example.voice_moderation.domain.audio.AudioStreamController
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

class AudioStreamer @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val audioProcessor: AudioProcessor
) : AudioStreamController {

    private val mutex = Mutex()
    private var audioRecord: AudioRecord? = null
    private var stateListener: ((Boolean) -> Unit)? = null

    @Volatile
    private var isStreaming = false

    private var audioReadingJob: Job? = null
    private val streamingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val sampleRate = 16000
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    override fun setStateListener(listener: (Boolean) -> Unit) {
        stateListener = listener
    }

    override fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun start() {
        streamingScope.launch {
            mutex.withLock {
                if (isStreaming) {
                    Timber.d("AudioStreamer already streaming, returning.")
                    return@launch
                }

                try {
                    if (!hasAudioPermission()) {
                        Timber.e("RECORD_AUDIO permission not granted")
                        return@launch
                    }

                    initializeAudioRecord()
                    startStreaming()
                } catch (e: Exception) {
                    Timber.e(e, "Error starting AudioStreamer")
                    handleStartError(e)
                }
            }
        }
    }

    private fun initializeAudioRecord() {
        cleanupAudioRecord()

        if (!hasAudioPermission()) {
            throw SecurityException("RECORD_AUDIO permission not granted")
        }

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            ).also { record ->
                if (record.state != AudioRecord.STATE_INITIALIZED) {
                    record.release()
                    throw IllegalStateException("AudioRecord failed to initialize. State: ${record.state}")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error initializing AudioRecord")
            throw e
        }
    }

    private fun startStreaming() {
        try {
            audioRecord?.startRecording()
            isStreaming = true
            stateListener?.invoke(true)

            audioReadingJob = streamingScope.launch {
                val buffer = ByteArray(bufferSize)
                try {
                    while (isStreaming) {
                        val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                        when {
                            read > 0 -> audioProcessor.processAudioData(buffer.copyOf(read))
                            read < 0 -> {
                                Timber.e("Error reading audio data: $read")
                                break
                            }
                            else -> Timber.d("No data read from audio record")
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error in audio reading loop")
                    stopStreaming()
                } finally {
                    Timber.d("Audio reading loop finished")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error starting audio recording")
            handleStartError(e)
        }
    }

    override fun stop() {
        streamingScope.launch {
            mutex.withLock {
                stopStreaming()
                cleanupAudioRecord()
            }
        }
    }

    private fun stopStreaming() {
        isStreaming = false
        stateListener?.invoke(false)
        audioReadingJob?.cancel()
        audioReadingJob = null
    }

    private fun handleStartError(error: Exception) {
        isStreaming = false
        stateListener?.invoke(false)
        cleanupAudioRecord()
        when (error) {
            is SecurityException -> Timber.e("Permission denied for audio recording")
            is IllegalStateException -> Timber.e("Audio recorder failed to initialize")
            else -> Timber.e("Unknown error in audio recording: ${error.message}")
        }
    }

    private fun cleanupAudioRecord() {
        try {
            audioRecord?.apply {
                stop()
                release()
            }
            audioRecord = null
            audioProcessor.clear()
        } catch (e: Exception) {
            Timber.e(e, "Error cleaning up AudioRecord")
        }
    }

    fun cleanup() {
        streamingScope.launch {
            mutex.withLock {
                stopStreaming()
                cleanupAudioRecord()
            }
        }
        streamingScope.cancel()
    }
}