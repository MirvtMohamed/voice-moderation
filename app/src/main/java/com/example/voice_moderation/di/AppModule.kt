package com.example.voice_moderation.di

import android.content.Context
import com.example.voice_moderation.data.remote.ApiClient
import com.example.voice_moderation.data.remote.RemoteDataSource
import com.example.voice_moderation.data.audio.AudioProcessor
import com.example.voice_moderation.data.audio.AudioStreamer
import com.example.voice_moderation.domain.audio.AudioStreamController
import com.example.voice_moderation.domain.usecase.AnalyzeVoiceUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiClient(): ApiClient {
        return ApiClient()
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(apiClient: ApiClient): RemoteDataSource {
        return RemoteDataSource(apiClient)
    }

    @Provides
    @Singleton
    fun provideAudioProcessor(
        analyzeVoiceUseCase: AnalyzeVoiceUseCase, // Updated dependency
        @ApplicationContext context: Context,
    ): AudioProcessor {
        return AudioProcessor(analyzeVoiceUseCase, context)
    }

    @Provides
    @Singleton
    fun provideAudioStreamer(
        @ApplicationContext context: Context,
        audioProcessor: AudioProcessor
    ): AudioStreamer {
        return AudioStreamer(context, audioProcessor)
    }

    @Provides
    @Singleton
    fun provideAudioStreamController(audioStreamer: AudioStreamer): AudioStreamController {
        return audioStreamer
    }
}