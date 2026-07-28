package com.tracktelemetry.recorder.presentation.recording

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
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
import com.tracktelemetry.recorder.presentation.theme.MotorsportRed

@Composable
fun RecordingScreen(
    speedKmh: Float = 142f,
    gLat: Float = 1.2f,
    onStopRecording: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onStopRecording() }, // Tap anywhere to stop
        color = DarkAsphalt
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // ── Top Row ──────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onStopRecording() }
                ) {
                    Box(
                        modifier = Modifier
                            .background(MotorsportRed, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("STOP / EXIT", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("LAP 3", color = DialWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Text("GPS 10Hz | OBD 2500 RPM", color = DialWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            // ── Predictive Time ──────────────────────────────────────────
            Text(
                text = "- 0.42",
                color = Color(0xFF4CAF50), // Green for faster
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            // ── Current Lap ─────────────────────────────────────────────
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "01:52.30",
                    color = DialWhite,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "CURRENT LAP",
                    color = DialWhite.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Bottom Stats ────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("LAST LAP: 01:52.72", color = DialWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("BEST LAP: 01:51.90", color = DialWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SPEED: ${speedKmh.toInt()} km/h", color = DialWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("G-FORCE: ${String.format("%.1f", gLat)}G", color = DialWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            // ── Tap to Stop Area ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(DarkGrayPanel)
                    .border(1.dp, MotorsportRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .clickable { onStopRecording() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "[⬛ TAP ANYWHERE TO STOP RECORDING]",
                    color = MotorsportRed,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}
