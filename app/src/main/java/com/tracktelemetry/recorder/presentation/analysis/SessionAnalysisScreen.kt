package com.tracktelemetry.recorder.presentation.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.tracktelemetry.recorder.presentation.history.SessionSummary
import com.tracktelemetry.recorder.presentation.theme.DarkAsphalt
import com.tracktelemetry.recorder.presentation.theme.DarkGrayPanel
import com.tracktelemetry.recorder.presentation.theme.DialWhite
import com.tracktelemetry.recorder.presentation.theme.MotorsportRed

private val fakeSpeeds = listOf(0f, 35f, 72f, 105f, 130f, 118f, 85f, 60f, 95f, 128f, 140f, 132f, 100f, 70f, 50f, 30f, 0f)
private val fakeLaps = listOf("Lap 1: 01:53.10", "Lap 2: 01:51.90 ⭐", "Lap 3")

@Composable
fun SessionAnalysisScreen(
    session: SessionSummary? = null,
    onBackClick: () -> Unit = {},
    onExportClick: () -> Unit = {}
) {
    Surface(modifier = Modifier.fillMaxSize(), color = DarkAsphalt) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // ── Top Bar ──────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DialWhite)
                    }
                    Text("[🔙 Back]", color = DialWhite, fontSize = 14.sp)
                }

                Text(
                    text = "${session?.trackName ?: "Chang Circuit"} (${session?.dateLabel ?: "28/07/2026"})",
                    color = DialWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onExportClick) {
                        Icon(Icons.Default.Upload, contentDescription = "Export", tint = DialWhite)
                    }
                    Text("[📤 Export]", color = DialWhite, fontSize = 14.sp)
                }
            }

            // ── Video Playback ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "[ VIDEO PLAYBACK ]\n(Shows Video + Gauge)",
                    color = DialWhite,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            // ── Middle Section (Track Map & Telemetry) ───────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Track Map
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text("🗺️ TRACK MAP TRACE", color = DialWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("(Shows Racing Line &\nCurrent Position Dot)", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                    Spacer(Modifier.height(8.dp))
                    
                    Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val trackPath = Path().apply {
                                moveTo(w * 0.1f, h * 0.7f)
                                lineTo(w * 0.1f, h * 0.25f)
                                quadraticTo(w * 0.1f, h * 0.1f, w * 0.3f, h * 0.1f)
                                lineTo(w * 0.75f, h * 0.1f)
                                quadraticTo(w * 0.9f, h * 0.1f, w * 0.9f, h * 0.3f)
                                lineTo(w * 0.9f, h * 0.55f)
                                quadraticTo(w * 0.9f, h * 0.75f, w * 0.65f, h * 0.8f)
                                lineTo(w * 0.3f, h * 0.8f)
                                quadraticTo(w * 0.1f, h * 0.8f, w * 0.1f, h * 0.7f)
                                close()
                            }
                            drawPath(trackPath, Color.White.copy(alpha = 0.15f), style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                            drawPath(trackPath, MotorsportRed, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                            drawCircle(MotorsportRed, radius = 4.dp.toPx(), center = Offset(w * 0.1f, h * 0.5f))
                        }
                    }
                }

                // Telemetry Graph
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text("📊 TELEMETRY GRAPH", color = DialWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Speed (km/h)", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                    Spacer(Modifier.height(8.dp))
                    
                    Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val maxSpeed = 200f
                            val stepX = w / (fakeSpeeds.size - 1)
                            
                            val path = Path()
                            fakeSpeeds.forEachIndexed { i, speed ->
                                val x = i * stepX
                                val y = h - (speed / maxSpeed * h)
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            drawPath(path, Color(0xFF4FC3F7), style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                            
                            // Y-axis labels approximate
                            drawLine(Color.White.copy(alpha=0.3f), Offset(0f, 0f), Offset(w, 0f))
                            drawLine(Color.White.copy(alpha=0.3f), Offset(0f, h/2), Offset(w, h/2))
                        }
                        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                            Text("200", color = Color.White.copy(alpha = 0.6f), fontSize = 8.sp)
                            Text("100", color = Color.White.copy(alpha = 0.6f), fontSize = 8.sp)
                            Text("0", color = Color.White.copy(alpha = 0.6f), fontSize = 8.sp)
                        }
                    }
                }
            }

            // ── Lap Selector Chips ───────────────────────────────────────
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(fakeLaps) { lapText ->
                    val isBest = lapText.contains("⭐")
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isBest) MotorsportRed.copy(alpha = 0.2f) else DarkGrayPanel)
                            .border(1.dp, if (isBest) MotorsportRed else Color.White.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("[ $lapText ]", color = DialWhite, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
