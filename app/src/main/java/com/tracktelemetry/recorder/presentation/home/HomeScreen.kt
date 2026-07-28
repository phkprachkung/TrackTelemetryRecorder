package com.tracktelemetry.recorder.presentation.home

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracktelemetry.recorder.presentation.theme.DarkAsphalt
import com.tracktelemetry.recorder.presentation.theme.DarkGrayPanel
import com.tracktelemetry.recorder.presentation.theme.DialWhite
import com.tracktelemetry.recorder.presentation.theme.MotorsportRed

data class TrackMode(val id: String, val label: String, val icon: ImageVector, val desc: String)
data class VehicleItem(val id: String, val name: String, val year: String)

@Composable
fun HomeScreen(
    onStartTrackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onCameraClick: () -> Unit = {}
) {
    val modes = listOf(
        TrackMode("circuit", "CIRCUIT", Icons.Default.Loop, "Full lap timing"),
        TrackMode("segment", "SEGMENT", Icons.Default.Timeline, "Point-to-point"),
        TrackMode("drag", "DRAG", Icons.Default.FastForward, "0-100 / Quarter mile"),
    )

    val vehicles = listOf(
        VehicleItem("v1", "My Car (Default)", "2024"),
        VehicleItem("v2", "Track Car #2", "2022"),
        VehicleItem("v3", "Bike #1", "2023"),
    )

    var selectedModeIndex by remember { mutableIntStateOf(0) }
    var selectedTrack by remember { mutableStateOf("No Track Selected") }
    var selectedVehicleIndex by remember { mutableIntStateOf(0) }
    var showVehicleDropdown by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = DarkAsphalt) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Top Bar ───────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(DarkGrayPanel)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = DialWhite)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TRACK TELEMETRY",
                        color = DialWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "RECORDER v2.0",
                        color = MotorsportRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }

                IconButton(
                    onClick = onCameraClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(DarkGrayPanel)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = "Camera", tint = DialWhite)
                }
            }

            // ── Mode Selector ─────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkGrayPanel)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    "SESSION MODE",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    modes.forEachIndexed { index, mode ->
                        val isSelected = selectedModeIndex == index
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected)
                                        Brush.verticalGradient(listOf(MotorsportRed.copy(alpha = 0.3f), MotorsportRed.copy(alpha = 0.1f)))
                                    else
                                        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.05f), Color.Transparent))
                                )
                                .border(
                                    1.5.dp,
                                    if (isSelected) MotorsportRed else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedModeIndex = index }
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                mode.icon,
                                contentDescription = null,
                                tint = if (isSelected) MotorsportRed else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                mode.label,
                                color = if (isSelected) MotorsportRed else DialWhite,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                letterSpacing = 1.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                mode.desc,
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 9.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            }

            // ── Track Selection ───────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkGrayPanel)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = MotorsportRed, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("TRACK SELECTION", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MotorsportRed, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        selectedTrack,
                        color = if (selectedTrack == "No Track Selected") Color.White.copy(alpha = 0.4f) else DialWhite,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Find Track button
                    OutlinedButton(
                        onClick = { selectedTrack = "Thailand International Circuit" },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DialWhite)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("FIND TRACK", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    // Custom Track button
                    OutlinedButton(
                        onClick = { selectedTrack = "Custom Track (GPS)" },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MotorsportRed)
                    ) {
                        Icon(Icons.Default.AddLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("CUSTOM", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ── Vehicle Selection ─────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkGrayPanel)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = MotorsportRed, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("VEHICLE", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
                Spacer(Modifier.height(10.dp))

                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                            .clickable { showVehicleDropdown = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(vehicles[selectedVehicleIndex].name, color = DialWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(vehicles[selectedVehicleIndex].year, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                        }
                        Icon(Icons.Default.ExpandMore, contentDescription = null, tint = Color.White.copy(alpha = 0.5f))
                    }

                    DropdownMenu(
                        expanded = showVehicleDropdown,
                        onDismissRequest = { showVehicleDropdown = false },
                        modifier = Modifier.background(DarkGrayPanel)
                    ) {
                        vehicles.forEachIndexed { index, vehicle ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(vehicle.name, color = DialWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(vehicle.year, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    selectedVehicleIndex = index
                                    showVehicleDropdown = false
                                },
                                leadingIcon = {
                                    if (index == selectedVehicleIndex) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MotorsportRed, modifier = Modifier.size(18.dp))
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Start Button ──────────────────────────────────────────
            Button(
                onClick = onStartTrackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MotorsportRed)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    "START TRACK SESSION",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }

            // ── Bottom Quick Tabs ─────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onHistoryClick,
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DialWhite)
                ) {
                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("SESSIONS", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                }
                OutlinedButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DialWhite)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("SETTINGS", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                }
            }
        }
    }
}
