package com.example.voice_moderation.presentation.components


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun AnimatedMicrophoneWaveform(
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_animation")

    // Wave animation
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_animation"
    )

    // Microphone pulse animation
    val micPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Animated waveform (only visible when recording)
        if (isRecording) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawWaveform(waveOffset)
            }
        }

        // Microphone icon with shadow and background
        Box(
            modifier = Modifier
                .size(80.dp * micPulse)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    spotColor = Color(0xFF38A8D2)
                )
                .clip(CircleShape)
                .background(
                    if (isRecording) Color(0xFF38A8D2) else Color.LightGray
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Mic else Icons.Default.MicOff,
                contentDescription = if (isRecording) "Microphone On" else "Microphone Off",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

private fun DrawScope.drawWaveform(waveOffset: Float) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val primaryColor = Color(0xFF38A8D2)

    // Draw circular waves
    for (i in 1..3) {
        val radius = 50f + (i * 30f)
        val amplitude = 5f + (i * 3f)
        val phase = waveOffset + (i * 45f)

        val path = Path()
        var isFirst = true

        for (angle in 0..360 step 5) {
            val radian = Math.toRadians(angle.toDouble())
            val waveRadius = radius + amplitude * sin(Math.toRadians((angle * 2 + phase).toDouble())).toFloat()

            val x = centerX + waveRadius * kotlin.math.cos(radian).toFloat()
            val y = centerY + waveRadius * kotlin.math.sin(radian).toFloat()

            if (isFirst) {
                path.moveTo(x, y)
                isFirst = false
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()

        drawPath(
            path = path,
            color = primaryColor.copy(alpha = 0.3f - (i * 0.08f)),
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }

    // Draw random audio spikes for more dynamic effect
    val random = Random(42)
    val spikeCount = 16

    for (i in 0 until spikeCount) {
        val angle = (i * 360f / spikeCount) + (waveOffset * 0.5f)
        val radian = Math.toRadians(angle.toDouble())

        // Random length for each spike
        val randomLength = 20.dp.toPx() + (random.nextFloat() * 40.dp.toPx())

        val startX = centerX + 50.dp.toPx() * kotlin.math.cos(radian).toFloat()
        val startY = centerY + 50.dp.toPx() * kotlin.math.sin(radian).toFloat()

        val endX = centerX + (50.dp.toPx() + randomLength) * kotlin.math.cos(radian).toFloat()
        val endY = centerY + (50.dp.toPx() + randomLength) * kotlin.math.sin(radian).toFloat()

        drawLine(
            color = primaryColor.copy(alpha = 0.4f),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedMicrophoneWaveformPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedMicrophoneWaveform(isRecording = true)
        AnimatedMicrophoneWaveform(isRecording = false)
    }
}