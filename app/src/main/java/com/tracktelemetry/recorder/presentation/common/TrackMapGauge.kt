package com.tracktelemetry.recorder.presentation.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracktelemetry.recorder.presentation.theme.DarkGrayPanel
import com.tracktelemetry.recorder.presentation.theme.DialWhite
import com.tracktelemetry.recorder.presentation.theme.MotorsportRed
import java.util.Locale

data class GpsPoint(val lat: Double, val lng: Double)

@Composable
fun TrackMapGauge(
    latitude: Double,
    longitude: Double,
    gpsHistory: List<GpsPoint> = emptyList(),
    modifier: Modifier = Modifier.size(width = 150.dp, height = 110.dp)
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkGrayPanel.copy(alpha = 0.85f))
            .border(1.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val width = size.width
            val height = size.height

            if (gpsHistory.size >= 2) {
                val minLat = gpsHistory.minOf { it.lat }
                val maxLat = gpsHistory.maxOf { it.lat }
                val minLng = gpsHistory.minOf { it.lng }
                val maxLng = gpsHistory.maxOf { it.lng }

                val latDelta = (maxLat - minLat).coerceAtLeast(0.00001)
                val lngDelta = (maxLng - minLng).coerceAtLeast(0.00001)

                val padding = 8.dp.toPx()
                val drawWidth = width - (padding * 2)
                val drawHeight = height - (padding * 2)

                val path = Path()
                var firstPoint = true

                gpsHistory.forEach { point ->
                    val x = padding + ((point.lng - minLng) / lngDelta * drawWidth).toFloat()
                    val y = padding + (drawHeight - ((point.lat - minLat) / latDelta * drawHeight)).toFloat()

                    if (firstPoint) {
                        path.moveTo(x, y)
                        firstPoint = false
                    } else {
                        path.lineTo(x, y)
                    }
                }

                // Draw Track Path from Real GPS Coordinates
                drawPath(
                    path = path,
                    color = MotorsportRed,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Current Car Position Dot
                val currentPoint = gpsHistory.last()
                val currentX = padding + ((currentPoint.lng - minLng) / lngDelta * drawWidth).toFloat()
                val currentY = padding + (drawHeight - ((currentPoint.lat - minLat) / latDelta * drawHeight)).toFloat()

                drawCircle(
                    color = MotorsportRed.copy(alpha = 0.4f),
                    radius = 9.dp.toPx(),
                    center = Offset(currentX, currentY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = Offset(currentX, currentY)
                )
            } else {
                // Initializing state when waiting for GPS track points
                val center = Offset(width / 2f, height / 2f)
                drawCircle(
                    color = MotorsportRed.copy(alpha = 0.4f),
                    radius = 8.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = center
                )
            }
        }

        // Header Label
        Text(
            text = "GPS MINIMAP",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp)
        )

        // Coordinates at bottom
        Text(
            text = if (latitude == 0.0) "WAITING GPS..." else String.format(Locale.US, "%.4f, %.4f", latitude, longitude),
            color = DialWhite,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
        )
    }
}
