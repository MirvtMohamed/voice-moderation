package com.example.voice_moderation.presentation.splashscreen


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.ui.graphics.lerp // Import the correct lerp function

@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {}
) {
    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animation")

    // Wave animation - controls the movement of the bars
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_animation"
    )

    // Text fade in animation
    val textAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "text_fade"
    )

    // X mark animation (pulsing)
    val xMarkScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x_mark_scale"
    )

    // Check authentication status
    LaunchedEffect(Unit) {
        delay(3000) // Show splash for 3 seconds
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            onNavigateToMain()
        } else {
            onNavigateToAuth()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated linear voice bars with X mark
            Box(
                modifier = Modifier
                    .width(240.dp)
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawLinearVoiceWave(waveOffset)
                    drawXMark(xMarkScale)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App title with fade animation
            Text(
                text = "Voice Moderation",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38A8D2),
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Protecting conversations",
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.alpha(textAlpha)
            )
        }
    }
}

private fun DrawScope.drawLinearVoiceWave(waveOffset: Float) {
    val centerX = size.width / 2
    val centerY = size.height / 2

    // Use a purple color similar to the reference image
    val waveColor = Color(0xFF38A8D2) // Medium purple
    val angryColor = Color(0xFFE53E3E) // Red for angry emotion

    // Number of bars in the wave
    val barCount = 30

    // Width of each bar
    val barWidth = 4.dp.toPx()

    // Space between bars
    val barSpacing = 3.dp.toPx()

    // Total width of all bars and spaces
    val totalWidth = barCount * (barWidth + barSpacing) - barSpacing

    // Starting X position to center the wave
    val startX = centerX - totalWidth / 2

    // Random generator with fixed seed for consistent randomness
    val random = Random(42)

    // Draw the linear bars
    for (i in 0 until barCount) {
        // Calculate the x position of this bar
        val x = startX + i * (barWidth + barSpacing)

        // Calculate the height of this bar using sine wave with offset
        // Add randomness and make some bars taller for "angry" effect
        val angle = (i * 12 + waveOffset) % 360
        val normalizedHeight = (sin(Math.toRadians(angle.toDouble())) + 1) / 2 // 0 to 1

        // Add randomness and make some bars taller for "angry" effect
        val randomFactor = if (random.nextFloat() > 0.7f) 1.5f else 1.0f
        val heightMultiplier = 0.3f + normalizedHeight.toFloat() * 0.7f * randomFactor // Cast normalizedHeight to Float

        // Bar height - taller in the middle, shorter at edges, with randomness
        val maxHeight = size.height * 0.8f
        val barHeight = maxHeight * heightMultiplier

        // Determine if this is an "angry" bar (taller and redder)
        val isAngryBar = random.nextFloat() > 0.7f

        // Color blend based on height and random factor
        val colorBlendFactor = if (isAngryBar) 0.7f else normalizedHeight.toFloat() * 0.3f // Cast normalizedHeight to Float
        val barColor = lerp(waveColor, angryColor, colorBlendFactor)

        // Draw the bar
        drawLine(
            color = barColor,
            start = Offset(x + barWidth / 2, centerY - barHeight / 2),
            end = Offset(x + barWidth / 2, centerY + barHeight / 2),
            strokeWidth = barWidth,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawXMark(scale: Float) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val markSize = 40.dp.toPx() * scale
    val strokeWidth = 6.dp.toPx()
    val redColor = Color(0xFFE53E3E)

    // Draw X mark lines
    drawLine(
        color = redColor,
        start = Offset(centerX - markSize/2, centerY - markSize/2),
        end = Offset(centerX + markSize/2, centerY + markSize/2),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )

    drawLine(
        color = redColor,
        start = Offset(centerX + markSize/2, centerY - markSize/2),
        end = Offset(centerX - markSize/2, centerY + markSize/2),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )

    // Draw circle background for X mark
    drawCircle(
        color = redColor.copy(alpha = 0.1f),
        radius = markSize/2 + 8.dp.toPx(),
        center = Offset(centerX, centerY)
    )

    drawCircle(
        color = redColor,
        radius = markSize/2 + 8.dp.toPx(),
        center = Offset(centerX, centerY),
        style = Stroke(width = 2.dp.toPx())
    )
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}

