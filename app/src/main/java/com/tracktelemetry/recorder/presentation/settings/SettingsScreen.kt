package com.tracktelemetry.recorder.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracktelemetry.recorder.presentation.theme.DarkAsphalt
import com.tracktelemetry.recorder.presentation.theme.DialWhite

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = DarkAsphalt) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // ── Top Bar ──────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DialWhite)
                }
                Text("⚙️ SETTINGS", color = DialWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // ── Hardware Connections ─────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("HARDWARE CONNECTIONS", color = DialWhite.copy(alpha = 0.6f), fontSize = 14.sp)
                SettingsRow("🔌 OBD-II Bluetooth", "Connected (vLinker) >")
                SettingsRow("📡 External GPS (10Hz)", "Connected (Garmin) >")
            }

            // ── Video & Audio ────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("VIDEO & AUDIO", color = DialWhite.copy(alpha = 0.6f), fontSize = 14.sp)
                SettingsRow("📹 Video Resolution", "1080p 60fps >")
                SettingsRow("🎙️ Record Audio", "ON >")
            }

            // ── Units & General ──────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("UNITS & GENERAL", color = DialWhite.copy(alpha = 0.6f), fontSize = 14.sp)
                SettingsRow("📏 Speed Unit", "Metric (km/h) >")
                SettingsRow("🌡️ Temp Unit", "Celsius (°C) >")
            }
        }
    }
}

@Composable
fun SettingsRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = DialWhite, fontSize = 16.sp)
        Text("[ $value ]", color = DialWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
