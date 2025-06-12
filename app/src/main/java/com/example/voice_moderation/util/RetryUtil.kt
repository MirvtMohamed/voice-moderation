package com.example.voice_moderation.util

import kotlinx.coroutines.delay
import timber.log.Timber
import java.io.IOException

suspend fun <T> retryWithExponentialBackoff(
    maxRetries: Int = 3,
    initialDelayMillis: Long = 1000,
    factor: Double = 2.0,
    shouldRetry: (Exception) -> Boolean = { it is IOException }, // Default to retry on network errors
    block: suspend () -> T
): T {
    var currentDelay = initialDelayMillis
    var retries = 0

    while (true) {
        try {
            return block()
        } catch (e: Exception) {
            if (retries < maxRetries && shouldRetry(e)) {
                Timber.w(e, "Retry attempt %d/%d for operation. Retrying in %d ms", retries + 1, maxRetries, currentDelay)
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong()
                retries++
            } else {
                Timber.e(e, "Operation failed after %d retries.", retries)
                throw e // Re-throw the exception if max retries reached or not a retriable error
            }
        }
    }
}