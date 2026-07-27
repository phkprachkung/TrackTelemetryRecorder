package com.tracktelemetry.recorder.presentation.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracktelemetry.recorder.presentation.theme.DarkGrayPanel
import com.tracktelemetry.recorder.presentation.theme.DialWhite
import com.tracktelemetry.recorder.presentation.theme.MotorsportRed
import java.util.Locale

@Composable
fun SpeedometerGauge(
    speedKmh: Float,
    maxSpeed: Float = 240f,
    modifier: Modifier = Modifier.size(120.dp)
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(DarkGrayPanel.copy(alpha = 0.8f))
            .border(1.5.dp, Color.White.copy(alpha = 0.25f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val startAngle = 140f
            val sweepAngle = 260f
            val currentSweep = ((speedKmh.coerceIn(0f, maxSpeed) / maxSpeed) * sweepAngle)

            // Background speed arc
            drawArc(
                color = Color.White.copy(alpha = 0.15f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )

            // Active Motorsport Red speed arc
            drawArc(
                color = MotorsportRed,
                startAngle = startAngle,
                sweepAngle = currentSweep,
                useCenter = false,
                style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format(Locale.US, "%.0f", speedKmh),
                color = DialWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "KM/H",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
