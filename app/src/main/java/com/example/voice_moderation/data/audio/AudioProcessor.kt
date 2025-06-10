package com.example.voice_moderation.data.audio

import android.content.Context
import com.example.voice_moderation.data.audio.util.WavFileWriter
import com.example.voice_moderation.domain.usecase.AnalyzeVoiceUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentLinkedDeque

class AudioProcessor @Inject constructor(
    private val analyzeVoiceUseCase: AnalyzeVoiceUseCase,
    @ApplicationContext private val context: Context
) {

    private val processingScope = CoroutineScope(Dispatchers.IO)
    private var processingJob: Job? = null

    // Audio format constants (should match AudioStreamer)
    private val SAMPLE_RATE = 16000
    private val NUM_CHANNELS = 1 // Mono
    private val BIT_DEPTH = 16
    private val BYTES_PER_SAMPLE = BIT_DEPTH / 8

    // Chunking parameters
    private val CHUNK_DURATION_MS = 5000L // 5 seconds per audio chunk
    private val SAMPLES_PER_CHUNK = (SAMPLE_RATE * CHUNK_DURATION_MS / 1000).toInt()
    private val BYTES_PER_CHUNK = SAMPLES_PER_CHUNK * BYTES_PER_SAMPLE * NUM_CHANNELS

    private val audioBufferQueue = ConcurrentLinkedDeque<ByteArray>() // Changed to ConcurrentLinkedDeque
    private var currentBufferBytes = 0

    fun processAudioData(audioData: ByteArray) {
        audioBufferQueue.add(audioData)
        currentBufferBytes += audioData.size

        // Start processing job if not already running
        if (processingJob == null || processingJob?.isActive == false) {
            startProcessingJob()
        }
    }

    private fun startProcessingJob() {
        processingJob = processingScope.launch {
            while (true) {
                if (currentBufferBytes >= BYTES_PER_CHUNK) {
                    val chunk = extractAudioChunk()
                    if (chunk != null) {
                        processChunkForApi(chunk)
                    }
                } else {
                    // Wait for more audio data if not enough for a full chunk
                    delay(100) // Small delay to prevent busy-waiting
                }
            }
        }
    }

    private fun extractAudioChunk(): ByteArray? {
        if (currentBufferBytes < BYTES_PER_CHUNK) {
            return null
        }

        val chunkBuffer = ByteArray(BYTES_PER_CHUNK)
        var bytesCopied = 0

        while (bytesCopied < BYTES_PER_CHUNK && audioBufferQueue.isNotEmpty()) {
            val head = audioBufferQueue.peek()
            val bytesToCopy = minOf(head.size, BYTES_PER_CHUNK - bytesCopied)

            System.arraycopy(head, 0, chunkBuffer, bytesCopied, bytesToCopy)
            bytesCopied += bytesToCopy

            if (bytesToCopy == head.size) {
                audioBufferQueue.poll() // Remove fully consumed buffer
            } else {
                // Partial consumption, create a new array for the remainder
                val remaining = ByteArray(head.size - bytesToCopy)
                System.arraycopy(head, bytesToCopy, remaining, 0, remaining.size)
                audioBufferQueue.poll()
                audioBufferQueue.addFirst(remaining) // Changed to addFirst()
            }
        }
        currentBufferBytes -= BYTES_PER_CHUNK
        return chunkBuffer
    }

    private fun processChunkForApi(audioChunk: ByteArray) {
        processingScope.launch {
            val tempFile = File(context.cacheDir, "audio_chunk_${System.currentTimeMillis()}.wav")
            try {
                WavFileWriter.writeWavFile(
                    tempFile,
                    audioChunk,
                    SAMPLE_RATE,
                    NUM_CHANNELS,
                    BIT_DEPTH
                )
                Timber.d("WAV file created: %s", tempFile.absolutePath)

                analyzeVoiceUseCase(tempFile).collect {
                    // Handle the ApiResult from the use case
                    // e.g., log, update a shared flow for UI, etc.
                    Timber.d("Audio analysis result: %s", it)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error processing audio chunk for API")
            } finally {
                if (tempFile.exists()) {
                    tempFile.delete() // Clean up the temporary file
                    Timber.d("Deleted temporary WAV file: %s", tempFile.absolutePath)
                }
            }
        }
    }

    fun clear() {
        processingJob?.cancel() // Cancel any ongoing processing
        audioBufferQueue.clear() // Clear all accumulated audio data
        currentBufferBytes = 0
        Timber.d("AudioProcessor cleared and processing job cancelled.")
    }
}
