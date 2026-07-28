package com.tracktelemetry.recorder.presentation.dashboard

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tracktelemetry.recorder.presentation.common.GForceGauge
import com.tracktelemetry.recorder.presentation.theme.DarkAsphalt
import com.tracktelemetry.recorder.presentation.theme.DarkGrayPanel
import com.tracktelemetry.recorder.presentation.theme.DialWhite
import com.tracktelemetry.recorder.presentation.theme.MotorsportRed

@Composable
fun DashboardScreen(
    onBackToMenuClick: () -> Unit,
    onStartRecording: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    var permissionsGranted by remember { mutableStateOf(false) }

    val requiredPermissions = remember {
        mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        permissionsGranted = result.values.all { it }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(requiredPermissions)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkAsphalt
    ) {
        if (!permissionsGranted) {
            PermissionRequestPlaceholder(onGrantClick = { permissionLauncher.launch(requiredPermissions) })
        } else {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Camera Preview (Fills Screen)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                viewModel.cameraManager.bindCamera(
                                    context = ctx,
                                    lifecycleOwner = lifecycleOwner,
                                    previewView = this
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // UI Overlay based on wireframe
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    
                    // ── Top Bar ───────────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBackToMenuClick) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DialWhite)
                            }
                            Text("[🔙 Back]", color = DialWhite, fontSize = 14.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { /* Flip Camera */ }) {
                                Icon(Icons.Default.Cameraswitch, contentDescription = "Flip Cam", tint = DialWhite)
                            }
                            Text("[📷 Flip Cam]", color = DialWhite, fontSize = 14.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { /* Video Res */ }) {
                                Icon(Icons.Default.Settings, contentDescription = "Video Res", tint = DialWhite)
                            }
                            Text("[⚙️ Video Res]", color = DialWhite, fontSize = 14.sp)
                        }
                    }

                    // ── Middle Section ────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // Left Info Box
                        Column(
                            modifier = Modifier
                                .border(1.dp, Color.White.copy(alpha = 0.5f))
                                .background(DarkGrayPanel.copy(alpha = 0.7f))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("SPEED: ${uiState.speedKmh.toInt()} km/h", color = DialWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("LAP: 00:00.00", color = DialWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        // G-Force Meter in center (adjusting layout a bit to fit it between the two)
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            GForceGauge(gLat = uiState.gLat, gLong = uiState.gLong)
                        }

                        // Right Status Badges
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text("[📍 GPS: OK 10Hz]", color = DialWhite, fontSize = 14.sp, modifier = Modifier.background(DarkGrayPanel.copy(alpha = 0.7f)).padding(4.dp))
                            Text("[🔌 OBD: Active]", color = DialWhite, fontSize = 14.sp, modifier = Modifier.background(DarkGrayPanel.copy(alpha = 0.7f)).padding(4.dp))
                        }
                    }

                    // ── Bottom Section ────────────────────────────────────
                    Button(
                        onClick = onStartRecording,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGrayPanel)
                    ) {
                        Text(
                            "[🔴 START RECORDING]",
                            color = DialWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionRequestPlaceholder(onGrantClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Videocam,
            contentDescription = null,
            tint = MotorsportRed,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Camera & Location Permission Required",
            style = MaterialTheme.typography.titleLarge,
            color = DialWhite
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Track Telemetry Recorder requires Camera, Audio, and Location permissions to record video and speed data.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onGrantClick,
            colors = ButtonDefaults.buttonColors(containerColor = MotorsportRed)
        ) {
            Text("Grant Permissions", color = Color.White)
        }
    }
}
