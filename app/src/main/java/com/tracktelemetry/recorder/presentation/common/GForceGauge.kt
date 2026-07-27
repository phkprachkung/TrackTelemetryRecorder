package com.tracktelemetry.recorder.presentation.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracktelemetry.recorder.presentation.theme.DarkGrayPanel
import com.tracktelemetry.recorder.presentation.theme.DialWhite
import com.tracktelemetry.recorder.presentation.theme.MotorsportRed
import java.util.Locale

@Composable
fun GForceGauge(
    gLat: Float,
    gLong: Float,
    modifier: Modifier = Modifier.size(110.dp)
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(DarkGrayPanel.copy(alpha = 0.8f))
            .border(1.5.dp, Color.White.copy(alpha = 0.25f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.width / 2f

            // Outer G rings (0.5G, 1.0G, 1.5G)
            drawCircle(
                color = Color.White.copy(alpha = 0.2f),
                radius = maxRadius * 0.33f,
                style = Stroke(width = 1f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = maxRadius * 0.66f,
                style = Stroke(width = 1f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = maxRadius,
                style = Stroke(width = 1.5f)
            )

            // Crosshair lines
            drawLine(
                color = Color.White.copy(alpha = 0.25f),
                start = Offset(center.x, 0f),
                end = Offset(center.x, size.height),
                strokeWidth = 1f
            )
            drawLine(
                color = Color.White.copy(alpha = 0.25f),
                start = Offset(0f, center.y),
                end = Offset(size.width, center.y),
                strokeWidth = 1f
            )

            // Plot G-force dot (Max G clamped to 1.5G)
            val clampedGLat = gLat.coerceIn(-1.5f, 1.5f)
            val clampedGLong = gLong.coerceIn(-1.5f, 1.5f)

            val ballX = center.x + (clampedGLat / 1.5f) * maxRadius
            val ballY = center.y - (clampedGLong / 1.5f) * maxRadius // Invert Y for forward Accel

            // Draw red G dot
            drawCircle(
                color = MotorsportRed,
                radius = 7.dp.toPx(),
                center = Offset(ballX, ballY)
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = Offset(ballX, ballY)
            )
        }

        // Numerical G readout at bottom
        Text(
            text = String.format(Locale.US, "%.1fG", kotlin.math.sqrt(gLat * gLat + gLong * gLong)),
            color = DialWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
        )
    }
}
