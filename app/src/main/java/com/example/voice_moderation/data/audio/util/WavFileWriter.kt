package com.example.voice_moderation.data.audio.util

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

object WavFileWriter {

    /**
     * Writes raw PCM audio data to a WAV file.
     *
     * @param outputFile The File object where the WAV data will be written.
     * @param audioData The raw PCM audio data (16-bit, mono).
     * @param sampleRate The sample rate of the audio (e.g., 16000).
     * @param numChannels The number of audio channels (e.g., 1 for mono).
     * @param bitDepth The bit depth of the audio (e.g., 16).
     */
    @Throws(IOException::class)
    fun writeWavFile(outputFile: File, audioData: ByteArray, sampleRate: Int, numChannels: Int, bitDepth: Int) {
        FileOutputStream(outputFile).use { fos ->
            val totalAudioLen = audioData.size.toLong()
            val totalDataLen = totalAudioLen + 36
            val longSampleRate = sampleRate.toLong()
            val byteRate = (bitDepth * sampleRate * numChannels / 8).toLong()
            val bytesPerFrame = (numChannels * bitDepth / 8).toShort()

            writeString(fos, "RIFF") // chunk id
            writeInt(fos, totalDataLen.toInt()) // chunk size
            writeString(fos, "WAVE") // format

            writeString(fos, "fmt ") // subchunk1 id
            writeInt(fos, 16) // subchunk1 size (16 for PCM)
            writeShort(fos, 1) // audio format (1 for PCM)
            writeShort(fos, numChannels.toShort()) // num channels
            writeInt(fos, longSampleRate.toInt()) // sample rate
            writeInt(fos, byteRate.toInt()) // byte rate
            writeShort(fos, bytesPerFrame) // block align
            writeShort(fos, bitDepth.toShort()) // bits per sample

            writeString(fos, "data") // subchunk2 id
            writeInt(fos, totalAudioLen.toInt()) // subchunk2 size

            fos.write(audioData) // audio data
        }
    }

    private fun writeInt(fos: FileOutputStream, value: Int) {
        val buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value)
        fos.write(buffer.array())
    }

    private fun writeShort(fos: FileOutputStream, value: Short) {
        val buffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value)
        fos.write(buffer.array())
    }

    private fun writeString(fos: FileOutputStream, s: String) {
        for (i in 0 until s.length) {
            fos.write(s[i].code)
        }
    }
}