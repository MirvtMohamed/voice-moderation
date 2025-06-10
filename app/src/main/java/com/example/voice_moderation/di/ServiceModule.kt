
package com.example.voice_moderation.di

import com.example.voice_moderation.data.preferences.SharedPreferencesAlertThresholdProvider
import com.example.voice_moderation.data.service.EmailAlertSendingService
import com.example.voice_moderation.domain.preferences.AlertThresholdProvider
import com.example.voice_moderation.domain.service.AlertSendingService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindAlertSendingService(
        emailAlertSendingService: EmailAlertSendingService
    ): AlertSendingService

    @Binds
    @Singleton
    abstract fun bindAlertThresholdProvider(
        sharedPreferencesAlertThresholdProvider: SharedPreferencesAlertThresholdProvider
    ): AlertThresholdProvider
}