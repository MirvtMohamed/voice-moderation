
package com.example.voice_moderation.di

import com.example.voice_moderation.data.repository.VoiceAnalysisRepositoryImpl
import com.example.voice_moderation.domain.repository.VoiceAnalysisRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVoiceAnalysisRepository(
        voiceAnalysisRepositoryImpl: VoiceAnalysisRepositoryImpl
    ): VoiceAnalysisRepository
}