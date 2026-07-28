package com.tracktelemetry.recorder.presentation.dashboard

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Stop
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
import com.tracktelemetry.recorder.presentation.common.SpeedometerGauge
import com.tracktelemetry.recorder.presentation.common.TrackMapGauge
import com.tracktelemetry.recorder.presentation.theme.DarkAsphalt
import com.tracktelemetry.recorder.presentation.theme.DarkGrayPanel
import com.tracktelemetry.recorder.presentation.theme.DialWhite
import com.tracktelemetry.recorder.presentation.theme.MotorsportRed
import java.util.Locale

@Composable
fun DashboardScreen(
    onBackToMenuClick: () -> Unit,
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

    LaunchedEffect(uiState.lastRecordedUri) {
        uiState.lastRecordedUri?.let { uri ->
            Toast.makeText(context, "Video & CSV Saved to Gallery!", Toast.LENGTH_LONG).show()
        }
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

                // Top HUD Status Bar
                TopHudHeader(
                    isRecording = uiState.isRecording,
                    durationSeconds = uiState.durationSeconds,
                    statusText = uiState.statusText,
                    onBackClick = onBackToMenuClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )

                // Bottom Left Telemetry Overlay Gauges (Speed + G-Force + Real GPS Minimap)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    SpeedometerGauge(speedKmh = uiState.speedKmh)
                    GForceGauge(gLat = uiState.gLat, gLong = uiState.gLong)
                    TrackMapGauge(
                        latitude = uiState.latitude,
                        longitude = uiState.longitude,
                        gpsHistory = uiState.gpsHistory
                    )
                }

                // Right Side Record Control Panel
                RightControlPanel(
                    isRecording = uiState.isRecording,
                    onRecordToggle = { viewModel.toggleRecording(context) },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(24.dp)
                )
            }
        }
    }
}

@Composable
fun TopHudHeader(
    isRecording: Boolean,
    durationSeconds: Long,
    statusText: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkGrayPanel.copy(alpha = 0.85f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Menu",
                    tint = DialWhite
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isRecording) MotorsportRed else Color.Gray)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                color = DialWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }

        Text(
            text = formatDuration(durationSeconds),
            color = if (isRecording) MotorsportRed else DialWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Text(
            text = "1080P • 60FPS",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun RightControlPanel(
    isRecording: Boolean,
    onRecordToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onRecordToggle,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(if (isRecording) MotorsportRed else Color.White)
                .border(3.dp, MotorsportRed, CircleShape)
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                tint = if (isRecording) Color.White else MotorsportRed,
                modifier = Modifier.size(36.dp)
            )
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

private fun formatDuration(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.US, "%02d:%02d", mins, secs)
}
