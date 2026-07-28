package com.tracktelemetry.recorder.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun HomeScreen(
    onStartTrackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    val modes = listOf("CIRCUIT MODE", "SEGMENT / HILLCLIMB", "DRAG 0-100")
    var selectedModeIndex by remember { mutableIntStateOf(0) }

    Surface(modifier = Modifier.fillMaxSize(), color = DarkAsphalt) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ── Top Bar ───────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = DialWhite)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            selectedModeIndex = if (selectedModeIndex > 0) selectedModeIndex - 1 else modes.size - 1
                        }
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Mode", tint = DialWhite)
                    }
                    Text(
                        text = modes[selectedModeIndex],
                        color = DialWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(180.dp)
                    )
                    IconButton(
                        onClick = {
                            selectedModeIndex = (selectedModeIndex + 1) % modes.size
                        }
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next Mode", tint = DialWhite)
                    }
                }

                IconButton(onClick = onCameraClick) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = DialWhite)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Track Selection Box ───────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.3f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏁", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "TRACK: Chang International Circuit",
                        color = DialWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = "[ Change Track / Create Custom GPS Track ]",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }

            // ── Vehicle Selection Box ─────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.3f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🚗", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "VEHICLE: Honda Civic Type R (FK8)",
                        color = DialWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = "[ Select / Add New Vehicle ]",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }

            Spacer(Modifier.weight(1f))

            // ── Primary Button ────────────────────────────────────────
            Button(
                onClick = onStartTrackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGrayPanel)
            ) {
                Text(
                    "[ GO TO CAMERA PREVIEW ]",
                    color = DialWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(Modifier.height(16.dp))
        }
    }
}
