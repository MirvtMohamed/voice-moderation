

package com.example.voice_moderation.data.service

import com.example.voice_moderation.data.remote.ApiResult
import com.example.voice_moderation.data.remote.RemoteDataSource
import com.example.voice_moderation.data.model.api.SendEmailRequest
import com.example.voice_moderation.domain.model.AudioAnalysisResult
import com.example.voice_moderation.domain.service.AlertSendingService
import com.example.voice_moderation.util.retryWithExponentialBackoff // Import the retry utility
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import java.io.IOException // Import IOException for shouldRetry
import javax.inject.Inject


class EmailAlertSendingService @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val firebaseAuth: FirebaseAuth
) : AlertSendingService {

    // <--- THIS LINE IS CORRECT, it now matches the updated interface
    override suspend fun sendAlert(analysisResult: AudioAnalysisResult) {
        val recipientEmail = firebaseAuth.currentUser?.email

        if (recipientEmail == null) {
            Timber.e("No authenticated user email found for sending alert. Alert not sent.")
            return
        }

        val request = SendEmailRequest(
            recipientEmail = recipientEmail,
            transcription = analysisResult.transcription,
            emotion = analysisResult.emotion,
            hateSpeechDetected = analysisResult.hateSpeechDetected,
            threatDetected = analysisResult.threatDetected,
            severityLevel = analysisResult.severityLevel
        )

        try {
            retryWithExponentialBackoff(maxRetries = 3, initialDelayMillis = 1000) {
                when (val result = remoteDataSource.sendAlertEmail(request)) {
                    is ApiResult.Success -> {
                        Timber.d("Email alert sent successfully to %s: %s", recipientEmail, result.data["message"])
                        // <--- REMOVE THE 'return' STATEMENT FROM HERE
                        // The block simply completes successfully.
                    }
                    is ApiResult.Error -> {
                        if (result.exception is IOException) {
                            throw result.exception // Re-throw to trigger retry
                        } else {
                            Timber.e(result.exception, "Non-retriable API error sending email alert to %s", recipientEmail)
                            throw result.exception // Re-throw to exit retry loop and propagate error
                        }
                    }
                    ApiResult.Loading -> { /* Should not happen for suspend function */ }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Final failure to send email alert to %s after retries.", recipientEmail)
            // TODO: Consider more robust error reporting here, e.g., to Crashlytics or a local database for later sync.
            // TODO: Potentially notify the user if this is a critical, unrecoverable failure.
        }
    }
}
