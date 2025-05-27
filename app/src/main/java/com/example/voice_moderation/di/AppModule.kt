package com.example.voice_moderation.di



import android.content.Context
import com.example.voice_moderation.data.audio.AudioProcessor
import com.example.voice_moderation.data.audio.AudioStreamController
import com.example.voice_moderation.data.audio.AudioStreamer
import com.example.voice_moderation.data.model.domain.repository.VoiceRepository
import com.example.voice_moderation.data.preferences.AlertThresholdProvider
import com.example.voice_moderation.data.preferences.SharedPreferencesAlertThresholdProvider
import com.example.voice_moderation.data.remote.ApiClient
import com.example.voice_moderation.data.remote.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Provides
//    fun provideAudioStreamer(
//        @ApplicationContext context: Context,
//        webSocketClient: WebSocketClient
//    ): AudioStreamer {
//        return AudioStreamer(context, webSocketClient)
//    }


//    @Provides
//    fun provideWebSocketClient(): WebSocketClient {
//        return WebSocketClient() // or inject dependencies if needed
//    }

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
    fun provideVoiceRepository(
        remoteDataSource: RemoteDataSource,
        alertThresholdProvider: AlertThresholdProvider
    ): VoiceRepository {
        return VoiceRepository(remoteDataSource, alertThresholdProvider)
    }


    @Provides
    @Singleton
    fun provideAudioProcessor(
        repository: VoiceRepository,
        @ApplicationContext context: Context,
    ): AudioProcessor {
        return AudioProcessor(repository, context)
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

    @Provides
    @Singleton
    fun provideAlertThresholdProvider(
        @ApplicationContext context: Context
    ): AlertThresholdProvider {
        val sharedPreferences = context.getSharedPreferences(
            "voice_monitor_preferences",
            Context.MODE_PRIVATE
        )
        return SharedPreferencesAlertThresholdProvider(sharedPreferences)
    }

}