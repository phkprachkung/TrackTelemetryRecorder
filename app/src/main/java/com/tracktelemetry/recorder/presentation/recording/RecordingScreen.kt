package com.tracktelemetry.recorder.presentation.recording

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracktelemetry.recorder.presentation.theme.DarkAsphalt
import com.tracktelemetry.recorder.presentation.theme.DarkGrayPanel
import com.tracktelemetry.recorder.presentation.theme.DialWhite
import com.tracktelemetry.recorder.presentation.theme.MotorsportRed
import java.util.Locale

enum class RecordingState { IDLE, STANDBY, RECORDING }

@Composable
fun RecordingScreen(
    speedKmh: Float = 0f,
    gLat: Float = 0f,
    gLong: Float = 0f,
    durationSeconds: Long = 0,
    currentLapTime: Long = 0,
    bestLapTime: Long = Long.MAX_VALUE,
    lapCount: Int = 0,
    sector1: Long = 0,
    sector2: Long = 0,
    isRecording: Boolean = false,
    onToggleRecord: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var recordingState by remember {
        mutableStateOf(if (isRecording) RecordingState.RECORDING else RecordingState.IDLE)
    }

    // Auto-start standby: when speed >= 15 km/h and in standby, auto-start recording
    LaunchedEffect(speedKmh, recordingState) {
        if (recordingState == RecordingState.STANDBY && speedKmh >= 15f) {
            recordingState = RecordingState.RECORDING
            onToggleRecord()
        }
    }

    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val isLapFaster = bestLapTime != Long.MAX_VALUE && currentLapTime < bestLapTime
    val deltaMs = if (bestLapTime != Long.MAX_VALUE) currentLapTime - bestLapTime else 0L

    Surface(modifier = Modifier.fillMaxSize(), color = DarkAsphalt) {
        Box(modifier = Modifier.fillMaxSize()) {

            // ── Tap whole screen to stop when recording ───────────────
            if (recordingState == RecordingState.RECORDING) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null
                        ) {
                            recordingState = RecordingState.IDLE
                            onToggleRecord()
                        }
                )
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ── Top Bar ───────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DialWhite)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        when (recordingState) {
                            RecordingState.IDLE -> {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color.Gray))
                                Text("READY", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                            }
                            RecordingState.STANDBY -> {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color.Yellow))
                                Text("AUTO-START STANDBY", color = Color.Yellow, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            }
                            RecordingState.RECORDING -> {
                                Box(modifier = Modifier
                                    .scale(if (isRecording) pulseScale else 1f)
                                    .size(10.dp).clip(CircleShape).background(MotorsportRed)
                                )
                                Text("REC ${formatDuration(durationSeconds)}", color = MotorsportRed, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                            }
                        }
                    }

                    Text(
                        "LAP $lapCount",
                        color = DialWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }

                // ── Predictive Lap Time + Sector Splits ───────────────
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Current Lap Timer (huge)
                    Text(
                        text = formatLapTime(currentLapTime),
                        color = DialWhite,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Delta vs Best Lap
                    if (bestLapTime != Long.MAX_VALUE) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (deltaMs <= 0) "▲ ${formatDeltaMs(-deltaMs)}" else "▼ +${formatDeltaMs(deltaMs)}",
                                color = if (isLapFaster) Color(0xFF4CAF50) else MotorsportRed,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Sector Splits Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SectorCard("S1", sector1, Modifier.weight(1f))
                        SectorCard("S2", sector2, Modifier.weight(1f))
                        SectorCard("S3", currentLapTime - sector1 - sector2, Modifier.weight(1f))
                    }
                }

                // ── Speed & G-Force ───────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TelemetryBadge("SPEED", "${speedKmh.toInt()}", "KM/H")
                    TelemetryBadge("G LAT", String.format(Locale.US, "%.2f", gLat), "G")
                    TelemetryBadge("G LONG", String.format(Locale.US, "%.2f", gLong), "G")
                    TelemetryBadge("BEST", if (bestLapTime == Long.MAX_VALUE) "--:--.---" else formatLapTime(bestLapTime), "")
                }

                // ── Record Control Button ─────────────────────────────
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AnimatedVisibility(
                        visible = recordingState == RecordingState.RECORDING,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            "TAP ANYWHERE TO STOP",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 12.sp,
                            letterSpacing = 2.sp
                        )
                    }

                    Box(contentAlignment = Alignment.Center) {
                        // Outer pulse ring (recording only)
                        if (recordingState == RecordingState.RECORDING) {
                            Box(
                                modifier = Modifier
                                    .scale(pulseScale)
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(MotorsportRed.copy(alpha = 0.2f))
                            )
                        }
                        // Main button
                        IconButton(
                            onClick = {
                                when (recordingState) {
                                    RecordingState.IDLE -> recordingState = RecordingState.STANDBY
                                    RecordingState.STANDBY -> {
                                        recordingState = RecordingState.RECORDING
                                        onToggleRecord()
                                    }
                                    RecordingState.RECORDING -> {
                                        recordingState = RecordingState.IDLE
                                        onToggleRecord()
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    when (recordingState) {
                                        RecordingState.IDLE -> MotorsportRed
                                        RecordingState.STANDBY -> Color(0xFFFFB300)
                                        RecordingState.RECORDING -> Color.White
                                    }
                                )
                                .border(4.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = when (recordingState) {
                                    RecordingState.IDLE -> Icons.Default.FiberManualRecord
                                    RecordingState.STANDBY -> Icons.Default.Pause
                                    RecordingState.RECORDING -> Icons.Default.Stop
                                },
                                contentDescription = null,
                                tint = when (recordingState) {
                                    RecordingState.IDLE -> Color.White
                                    RecordingState.STANDBY -> DarkAsphalt
                                    RecordingState.RECORDING -> MotorsportRed
                                },
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Text(
                        text = when (recordingState) {
                            RecordingState.IDLE -> "PRESS TO ARM"
                            RecordingState.STANDBY -> "AUTO-START AT 15 KM/H"
                            RecordingState.RECORDING -> "RECORDING IN PROGRESS"
                        },
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SectorCard(label: String, timeMs: Long, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkGrayPanel)
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (timeMs <= 0) "--" else formatLapTime(timeMs),
            color = DialWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TelemetryBadge(label: String, value: String, unit: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkGrayPanel)
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        Text(value, color = DialWhite, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text(unit, color = MotorsportRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

private fun formatDuration(seconds: Long): String {
    val m = seconds / 60; val s = seconds % 60
    return String.format(Locale.US, "%02d:%02d", m, s)
}

private fun formatLapTime(ms: Long): String {
    val m = ms / 60000; val s = (ms % 60000) / 1000; val millis = ms % 1000
    return String.format(Locale.US, "%d:%02d.%03d", m, s, millis)
}

private fun formatDeltaMs(ms: Long): String {
    val s = ms / 1000; val millis = ms % 1000
    return String.format(Locale.US, "%d.%03d", s, millis)
}
