package com.example.voice_moderation.data.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okhttp3.Request


class WebSocketClient {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()

    fun connect() {
        val request = Request.Builder()
            .url("wss://your-server.com/audio") // replace with your server
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                Log.d("WebSocket", "Connected")
            }

            override fun onMessage(ws: WebSocket, text: String) {
                Log.d("WebSocket", "Message: $text")
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Error: ${t.message}")
            }
        })
    }

    fun send(audioChunk: ByteArray) {
        // You can encode to Base64 if needed
        webSocket?.send(ByteString.of(*audioChunk))
    }

    fun disconnect() {
        webSocket?.close(1000, "Goodbye")
        webSocket = null
    }
}
