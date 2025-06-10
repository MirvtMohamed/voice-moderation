package com.example.voice_moderation.data.remote


import java.io.IOException

sealed class ApiException(message: String, cause: Throwable? = null) : IOException(message, cause) {
    class NetworkException(message: String = "Network error", cause: Throwable? = null) : ApiException(message, cause)
    class ServerException(val code: Int, message: String = "Server error", cause: Throwable? = null) : ApiException(message, cause)
    class SerializationException(message: String = "Data serialization error", cause: Throwable? = null) : ApiException(message, cause)
    class UnknownApiException(message: String = "An unknown API error occurred", cause: Throwable? = null) : ApiException(message, cause)
}