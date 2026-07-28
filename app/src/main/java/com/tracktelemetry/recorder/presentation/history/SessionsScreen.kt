package com.tracktelemetry.recorder.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracktelemetry.recorder.presentation.theme.DarkAsphalt
import com.tracktelemetry.recorder.presentation.theme.DarkGrayPanel
import com.tracktelemetry.recorder.presentation.theme.DialWhite
import com.tracktelemetry.recorder.presentation.theme.MotorsportRed

data class SessionSummary(
    val id: String,
    val trackName: String,
    val vehicleName: String,
    val dateLabel: String,
    val bestLapTime: String,
    val lapCount: Int,
    val hasVideo: Boolean,
    val hasGps: Boolean,
    val hasObd: Boolean
)

// Sample session data — replace with Room DB in Phase 5
private val sampleSessions = listOf(
    SessionSummary("s1", "Thailand International Circuit", "My Car (Default)", "28 Jul 2026, 10:32", "1:28.456", 12, true, true, false),
    SessionSummary("s2", "Custom Track (GPS)", "Track Car #2", "25 Jul 2026, 08:15", "0:52.112", 7, true, true, false),
    SessionSummary("s3", "Thailand International Circuit", "My Car (Default)", "20 Jul 2026, 15:50", "1:31.009", 5, false, true, false),
    SessionSummary("s4", "Bira International Circuit", "My Car (Default)", "15 Jul 2026, 09:00", "1:05.882", 18, true, true, true),
)

@Composable
fun SessionsScreen(
    onBackClick: () -> Unit,
    onSessionClick: (SessionSummary) -> Unit = {}
) {
    Surface(modifier = Modifier.fillMaxSize(), color = DarkAsphalt) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ─────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkGrayPanel)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DialWhite)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("SESSIONS", color = DialWhite, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Text("${sampleSessions.size} RECORDED SESSIONS", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp, letterSpacing = 1.sp)
                }
            }

            // ── Session List ──────────────────────────────────────────
            if (sampleSessions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FolderOff, contentDescription = null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("No sessions yet", color = Color.White.copy(alpha = 0.4f), fontSize = 16.sp)
                        Text("Start your first track session!", color = Color.White.copy(alpha = 0.25f), fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sampleSessions) { session ->
                        SessionCard(session = session, onClick = { onSessionClick(session) })
                    }
                }
            }
        }
    }
}

@Composable
fun SessionCard(session: SessionSummary, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkGrayPanel)
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Track Name + Date
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    session.trackName,
                    color = DialWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    session.dateLabel,
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "VEHICLE", value = session.vehicleName.take(15))
            VerticalDivider(modifier = Modifier.height(36.dp), color = Color.White.copy(alpha = 0.1f))
            StatItem(label = "BEST LAP", value = session.bestLapTime, valueColor = MotorsportRed)
            VerticalDivider(modifier = Modifier.height(36.dp), color = Color.White.copy(alpha = 0.1f))
            StatItem(label = "LAPS", value = "${session.lapCount}")
        }

        // Data Indicator Chips
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (session.hasVideo) DataChip(Icons.Default.Videocam, "VIDEO", Color(0xFF4FC3F7))
            if (session.hasGps) DataChip(Icons.Default.GpsFixed, "GPS", Color(0xFF81C784))
            if (session.hasObd) DataChip(Icons.Default.Speed, "OBD-II", Color(0xFFFFB74D))
        }
    }
}

@Composable
fun StatItem(label: String, value: String, valueColor: Color = DialWhite) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
fun DataChip(icon: ImageVector, label: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(11.dp))
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
