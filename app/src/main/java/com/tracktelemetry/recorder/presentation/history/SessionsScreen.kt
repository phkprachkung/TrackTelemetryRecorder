package com.tracktelemetry.recorder.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracktelemetry.recorder.presentation.theme.DarkAsphalt
import com.tracktelemetry.recorder.presentation.theme.DarkGrayPanel
import com.tracktelemetry.recorder.presentation.theme.DialWhite

data class SessionSummary(
    val id: String,
    val trackName: String,
    val dateLabel: String,
    val vehicleName: String,
    val bestLapTime: String,
    val lapCount: Int,
    val hasVideo: Boolean,
    val hasGps: Boolean,
    val hasObd: Boolean
)

private val sampleSessions = listOf(
    SessionSummary("s1", "Chang Circuit", "28/07/2026", "Honda Civic Type R", "01:51.90", 8, true, true, true),
    SessionSummary("s2", "Bira Circuit", "15/06/2026", "Honda Civic Type R", "01:12.45", 12, false, true, false)
)

@Composable
fun SessionsScreen(
    onBackClick: () -> Unit,
    onSessionClick: (SessionSummary) -> Unit = {}
) {
    Surface(modifier = Modifier.fillMaxSize(), color = DarkAsphalt) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Top Bar ──────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📂 SESSIONS HISTORY",
                    color = DialWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = "Filter", tint = DialWhite)
                    Spacer(Modifier.width(4.dp))
                    Text("[🔍 Filter]", color = DialWhite, fontSize = 14.sp)
                }
            }

            // ── Session List ──────────────────────────────────────────
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sampleSessions) { session ->
                    SessionCard(session = session, onClick = { onSessionClick(session) })
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
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .background(DarkGrayPanel)
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("🏁 ${session.trackName} - ${session.dateLabel}", color = DialWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("🚗 ${session.vehicleName}", color = DialWhite, fontSize = 14.sp)
        Text("⏱️ Best Lap: ${session.bestLapTime} (Total ${session.lapCount} Laps)", color = DialWhite, fontSize = 14.sp)
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📁", color = DialWhite, fontSize = 14.sp)
            if (session.hasVideo) {
                Text("[📹 Video]", color = DialWhite, fontSize = 14.sp)
            } else {
                Text("(No Video)", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
            }
            if (session.hasGps) {
                Text("[📍 GPS Data]", color = DialWhite, fontSize = 14.sp)
            }
            if (session.hasObd) {
                Text("[📊 OBD Data]", color = DialWhite, fontSize = 14.sp)
            }
        }
    }
}
