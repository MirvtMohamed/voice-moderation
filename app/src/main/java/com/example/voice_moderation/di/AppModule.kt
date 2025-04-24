package com.example.voice_moderation.di



import android.content.Context
import com.example.voice_moderation.data.audio.AudioStreamController
import com.example.voice_moderation.data.audio.AudioStreamer
import com.example.voice_moderation.data.network.WebSocketClient
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
    fun provideAudioStreamer(
        @ApplicationContext context: Context,
        webSocketClient: WebSocketClient
    ): AudioStreamer {
        return AudioStreamer(context, webSocketClient)
    }


    @Provides
    fun provideWebSocketClient(): WebSocketClient {
        return WebSocketClient() // or inject dependencies if needed
    }
}
