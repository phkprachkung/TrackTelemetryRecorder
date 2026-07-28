package com.tracktelemetry.recorder.presentation.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracktelemetry.recorder.presentation.history.SessionSummary
import com.tracktelemetry.recorder.presentation.theme.DarkAsphalt
import com.tracktelemetry.recorder.presentation.theme.DarkGrayPanel
import com.tracktelemetry.recorder.presentation.theme.DialWhite
import com.tracktelemetry.recorder.presentation.theme.MotorsportRed
import java.util.Locale

// Fake telemetry data for visualization
private val fakeSpeeds = listOf(0f, 35f, 72f, 105f, 130f, 118f, 85f, 60f, 95f, 128f, 140f, 132f, 100f, 70f, 50f, 30f, 0f)
private val fakeGLat = listOf(0f, 0.2f, 0.5f, 0.8f, 0.4f, -0.6f, -0.9f, -0.5f, 0.3f, 0.7f, 0.2f, -0.4f, -0.7f, -0.3f, 0.1f, 0f, 0f)

private val fakeLaps = listOf(
    Triple("LAP 1", 88456L, "Best"),
    Triple("LAP 2", 91023L, ""),
    Triple("LAP 3", 89874L, ""),
    Triple("LAP 4", 90212L, ""),
    Triple("LAP 5", 87991L, "+0.465"),
)

@Composable
fun SessionAnalysisScreen(
    session: SessionSummary? = null,
    onBackClick: () -> Unit = {},
    onExportClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("VIDEO", "TRACK MAP", "TELEMETRY", "LAPS")

    Surface(modifier = Modifier.fillMaxSize(), color = DarkAsphalt) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ─────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkGrayPanel)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DialWhite)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(session?.trackName ?: "Session Analysis", color = DialWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(session?.dateLabel ?: "", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                        }
                    }
                    IconButton(
                        onClick = onExportClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MotorsportRed.copy(alpha = 0.2f))
                            .border(1.dp, MotorsportRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Export", tint = MotorsportRed)
                    }
                }

                // Tab Row
                Row(modifier = Modifier.fillMaxWidth()) {
                    tabs.forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = index }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    label,
                                    color = if (isSelected) MotorsportRed else Color.White.copy(alpha = 0.4f),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    letterSpacing = 1.sp
                                )
                                if (isSelected) {
                                    Spacer(Modifier.height(6.dp))
                                    Box(Modifier.fillMaxWidth(0.5f).height(2.dp).background(MotorsportRed))
                                } else {
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }

            // ── Tab Content ───────────────────────────────────────────
            when (selectedTab) {
                0 -> VideoTab()
                1 -> TrackMapTab()
                2 -> TelemetryGraphTab()
                3 -> LapComparisonTab()
            }
        }
    }
}

@Composable
fun VideoTab() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Video placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.PlayCircleFilled, contentDescription = null, tint = MotorsportRed, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(8.dp))
                Text("VIDEO PLAYBACK", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp, letterSpacing = 2.sp)
                Text("ExoPlayer / Media3", color = Color.White.copy(alpha = 0.25f), fontSize = 11.sp)
            }
        }

        // Video controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                Icons.Default.SkipPrevious to "Prev Lap",
                Icons.Default.Replay5 to "–5s",
                Icons.Default.PlayArrow to "Play",
                Icons.Default.Forward5 to "+5s",
                Icons.Default.SkipNext to "Next Lap",
            ).forEach { (icon, label) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(DarkGrayPanel)
                            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(icon, contentDescription = label, tint = DialWhite, modifier = Modifier.size(22.dp))
                    }
                    Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
                }
            }
        }

        // Export buttons
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DialWhite)
            ) {
                Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("EXPORT CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = {},
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MotorsportRed)
            ) {
                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("RENDER VIDEO", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TrackMapTab() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkGrayPanel)
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("TRACK MAP — RACING LINE", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                val w = size.width
                val h = size.height

                // Draw track outline
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
                drawPath(trackPath, Color.White.copy(alpha = 0.15f), style = Stroke(width = 22.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(trackPath, MotorsportRed, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))

                // Start/Finish line
                drawLine(Color.White, start = Offset(w * 0.1f, h * 0.65f), end = Offset(w * 0.2f, h * 0.65f), strokeWidth = 3.dp.toPx())

                // Car position dot
                drawCircle(MotorsportRed.copy(alpha = 0.4f), radius = 12.dp.toPx(), center = Offset(w * 0.1f, h * 0.5f))
                drawCircle(Color.White, radius = 5.dp.toPx(), center = Offset(w * 0.1f, h * 0.5f))
            }

            Text("GPS Breadcrumb tracking active", color = MotorsportRed, fontSize = 12.sp)
        }
    }
}

@Composable
fun TelemetryGraphTab() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TelemetryGraph("SPEED (KM/H)", fakeSpeeds, Color(0xFF4FC3F7), maxValue = 150f)
        TelemetryGraph("G-FORCE LATERAL", fakeGLat, MotorsportRed, maxValue = 2f, showNegative = true)
    }
}

@Composable
fun TelemetryGraph(title: String, data: List<Float>, color: Color, maxValue: Float, showNegative: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkGrayPanel)
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(title, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.height(12.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            val w = size.width
            val h = size.height
            val midY = if (showNegative) h / 2f else h

            // Grid lines
            for (i in 0..4) {
                val y = h * (i / 4f)
                drawLine(Color.White.copy(alpha = 0.06f), Offset(0f, y), Offset(w, y), strokeWidth = 1f)
            }

            if (data.isEmpty()) return@Canvas
            val stepX = w / (data.size - 1).toFloat()

            // Fill area under curve
            val fillPath = Path().apply {
                data.forEachIndexed { i, v ->
                    val x = i * stepX
                    val normalizedV = (v / maxValue).coerceIn(-1f, 1f)
                    val y = midY - normalizedV * midY
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                lineTo((data.size - 1) * stepX, midY)
                lineTo(0f, midY)
                close()
            }
            drawPath(fillPath, color.copy(alpha = 0.15f))

            // Line
            val linePath = Path()
            data.forEachIndexed { i, v ->
                val x = i * stepX
                val normalizedV = (v / maxValue).coerceIn(-1f, 1f)
                val y = midY - normalizedV * midY
                if (i == 0) linePath.moveTo(x, y) else linePath.lineTo(x, y)
            }
            drawPath(linePath, color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))

            // Zero line (for G-Force)
            if (showNegative) {
                drawLine(Color.White.copy(alpha = 0.2f), Offset(0f, midY), Offset(w, midY), strokeWidth = 1f)
            }
        }
    }
}

@Composable
fun LapComparisonTab() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("LAP COMPARISON", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)

        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("LAP", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
            Text("TIME", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("DELTA", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
        }

        fakeLaps.forEach { (lapLabel, timeMs, note) ->
            val isBest = note == "Best"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isBest) MotorsportRed.copy(alpha = 0.1f) else DarkGrayPanel)
                    .border(1.dp, if (isBest) MotorsportRed.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(lapLabel, color = DialWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                Text(formatLapTime(timeMs), color = if (isBest) MotorsportRed else DialWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(
                    if (isBest) "BEST" else if (note.isNotEmpty()) note else "--",
                    color = when {
                        isBest -> MotorsportRed
                        note.startsWith("+") -> MotorsportRed.copy(alpha = 0.7f)
                        note.startsWith("-") -> Color(0xFF81C784)
                        else -> Color.White.copy(alpha = 0.3f)
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.8f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

private fun formatLapTime(ms: Long): String {
    val m = ms / 60000; val s = (ms % 60000) / 1000; val millis = ms % 1000
    return String.format(Locale.US, "%d:%02d.%03d", m, s, millis)
}
