package com.tracktelemetry.recorder.presentation.recording

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracktelemetry.recorder.presentation.theme.DarkAsphalt
import com.tracktelemetry.recorder.presentation.theme.DarkGrayPanel
import com.tracktelemetry.recorder.presentation.theme.DialWhite

@Composable
fun RecordingScreen(
    speedKmh: Float = 142f,
    gLat: Float = 1.2f,
    onStopRecording: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkAsphalt
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // ── Top Row ──────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("LAP 3", color = DialWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("GPS 10Hz | OBD 2500 RPM", color = DialWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(32.dp))

            // ── Predictive Time ──────────────────────────────────────────
            Text(
                text = "- 0.42",
                color = Color(0xFF4CAF50), // Green for faster
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // ── Current Lap ─────────────────────────────────────────────
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "01:52.30",
                    color = DialWhite,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "CURRENT LAP",
                    color = DialWhite.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            Spacer(Modifier.weight(1f))

            // ── Bottom Stats ────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("LAST LAP: 01:52.72", color = DialWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("BEST LAP: 01:51.90", color = DialWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SPEED: ${speedKmh.toInt()} km/h", color = DialWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("G-FORCE: ${String.format("%.1f", gLat)}G", color = DialWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(32.dp))

            // ── Tap to Stop Area ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(DarkGrayPanel)
                    .clickable { onStopRecording() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "[⬛ TAP ANYWHERE TO STOP]",
                    color = DialWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}
