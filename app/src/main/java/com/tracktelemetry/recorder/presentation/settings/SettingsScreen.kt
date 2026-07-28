package com.tracktelemetry.recorder.presentation.settings

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracktelemetry.recorder.presentation.theme.DarkAsphalt
import com.tracktelemetry.recorder.presentation.theme.DarkGrayPanel
import com.tracktelemetry.recorder.presentation.theme.DialWhite
import com.tracktelemetry.recorder.presentation.theme.MotorsportRed

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    var selectedResolution by remember { mutableStateOf("1080p 60fps") }
    var selectedUnit by remember { mutableStateOf("KM/H") }
    var obdConnected by remember { mutableStateOf(false) }
    var externalGpsConnected by remember { mutableStateOf(false) }

    val resolutions = listOf("720p 60fps", "1080p 60fps", "4K 30fps")
    val units = listOf("KM/H", "MPH")

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
                    Text("SETTINGS", color = DialWhite, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Text("SYSTEM CONFIGURATION", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp, letterSpacing = 1.sp)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ── OBD-II Setup ───────────────────────────────────────
                SettingsSection(
                    icon = Icons.Default.Speed,
                    title = "OBD-II HARDWARE",
                    subtitle = "Engine data via Bluetooth/Wi-Fi"
                ) {
                    DeviceConnectionCard(
                        label = "Bluetooth OBD-II Adapter",
                        connected = obdConnected,
                        iconColor = Color(0xFF4FC3F7),
                        onScanClick = { obdConnected = !obdConnected }
                    )
                    Spacer(Modifier.height(10.dp))
                    InfoChip("Engine RPM • Throttle % • Brake • Fuel Temp")
                }

                // ── External GPS Setup ─────────────────────────────────
                SettingsSection(
                    icon = Icons.Default.GpsFixed,
                    title = "EXTERNAL GPS",
                    subtitle = "High-accuracy 10Hz GPS receiver"
                ) {
                    DeviceConnectionCard(
                        label = "Bluetooth GPS Receiver",
                        connected = externalGpsConnected,
                        iconColor = Color(0xFF81C784),
                        onScanClick = { externalGpsConnected = !externalGpsConnected }
                    )
                    Spacer(Modifier.height(10.dp))
                    InfoChip("10Hz update rate • ±0.5m accuracy (vs 1-3m internal)")
                }

                // ── Video Settings ─────────────────────────────────────
                SettingsSection(
                    icon = Icons.Default.Videocam,
                    title = "VIDEO SETTINGS",
                    subtitle = "Recording quality & frame rate"
                ) {
                    resolutions.forEach { res ->
                        SettingsRadioRow(
                            label = res,
                            selected = selectedResolution == res,
                            onClick = { selectedResolution = res }
                        )
                    }
                }

                // ── Units System ───────────────────────────────────────
                SettingsSection(
                    icon = Icons.Default.Straighten,
                    title = "UNITS SYSTEM",
                    subtitle = "Speed and distance measurement"
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        units.forEach { unit ->
                            val isSelected = selectedUnit == unit
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) MotorsportRed.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
                                    .border(1.5.dp, if (isSelected) MotorsportRed else Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                                    .clickable { selectedUnit = unit }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    unit,
                                    color = if (isSelected) MotorsportRed else DialWhite,
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    letterSpacing = 2.sp
                                )
                            }
                        }
                    }
                }

                // ── App Info ───────────────────────────────────────────
                SettingsSection(
                    icon = Icons.Default.Info,
                    title = "ABOUT",
                    subtitle = "App version & developer info"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        AppInfoRow("Version", "2.0.0")
                        AppInfoRow("Build", "Phase 3 Complete")
                        AppInfoRow("Developer", "Track Telemetry Labs")
                        AppInfoRow("Repository", "github.com/phkprachkung")
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    icon: ImageVector,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkGrayPanel)
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(MotorsportRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MotorsportRed, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, color = DialWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                Text(subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
            }
        }
        HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
        content()
    }
}

@Composable
fun DeviceConnectionCard(
    label: String,
    connected: Boolean,
    iconColor: Color,
    onScanClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, if (connected) iconColor.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(10.dp).clip(CircleShape)
                    .background(if (connected) iconColor else Color.Gray)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(label, color = DialWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(if (connected) "CONNECTED" else "NOT CONNECTED", color = if (connected) iconColor else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
        OutlinedButton(
            onClick = onScanClick,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = if (connected) Color(0xFFEF5350) else iconColor)
        ) {
            Text(if (connected) "DISCONNECT" else "SCAN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
    }
}

@Composable
fun SettingsRadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) MotorsportRed.copy(alpha = 0.1f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = MotorsportRed, unselectedColor = Color.White.copy(alpha = 0.3f))
        )
        Text(
            label,
            color = if (selected) MotorsportRed else DialWhite,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun AppInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 13.sp)
        Text(value, color = DialWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
