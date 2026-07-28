package com.tracktelemetry.recorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracktelemetry.recorder.presentation.analysis.SessionAnalysisScreen
import com.tracktelemetry.recorder.presentation.dashboard.DashboardScreen
import com.tracktelemetry.recorder.presentation.history.SessionSummary
import com.tracktelemetry.recorder.presentation.history.SessionsScreen
import com.tracktelemetry.recorder.presentation.home.HomeScreen
import com.tracktelemetry.recorder.presentation.recording.RecordingScreen
import com.tracktelemetry.recorder.presentation.settings.SettingsScreen
import com.tracktelemetry.recorder.presentation.theme.DarkAsphalt
import com.tracktelemetry.recorder.presentation.theme.DarkGrayPanel
import com.tracktelemetry.recorder.presentation.theme.DialWhite
import com.tracktelemetry.recorder.presentation.theme.MotorsportRed
import com.tracktelemetry.recorder.presentation.theme.TrackTelemetryRecorderTheme
import dagger.hilt.android.AndroidEntryPoint

enum class Screen {
    HOME,
    DASHBOARD,
    RECORDING,
    SESSIONS,
    ANALYSIS,
    SETTINGS
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrackTelemetryRecorderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkAsphalt
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedSession by remember { mutableStateOf<SessionSummary?>(null) }

    // Check if we should show bottom navigation
    val showBottomNav = currentScreen == Screen.HOME || currentScreen == Screen.SESSIONS

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(
                    containerColor = DarkGrayPanel,
                    modifier = Modifier.height(64.dp)
                ) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.HOME,
                        onClick = { currentScreen = Screen.HOME },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MotorsportRed,
                            selectedTextColor = MotorsportRed,
                            unselectedIconColor = Color.White.copy(alpha = 0.5f),
                            unselectedTextColor = Color.White.copy(alpha = 0.5f),
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { currentScreen = Screen.DASHBOARD },
                        icon = { Icon(Icons.Default.Videocam, contentDescription = "Record") },
                        label = { Text("Record", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Color.White.copy(alpha = 0.5f),
                            unselectedTextColor = Color.White.copy(alpha = 0.5f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.SESSIONS,
                        onClick = { currentScreen = Screen.SESSIONS },
                        icon = { Icon(Icons.Default.Folder, contentDescription = "Sessions") },
                        label = { Text("Sessions", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MotorsportRed,
                            selectedTextColor = MotorsportRed,
                            unselectedIconColor = Color.White.copy(alpha = 0.5f),
                            unselectedTextColor = Color.White.copy(alpha = 0.5f),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DarkAsphalt)
        ) {
            when (currentScreen) {
                Screen.HOME -> HomeScreen(
                    onStartTrackClick = { currentScreen = Screen.DASHBOARD },
                    onSettingsClick = { currentScreen = Screen.SETTINGS },
                    onCameraClick = { currentScreen = Screen.DASHBOARD }
                )

                Screen.DASHBOARD -> DashboardScreen(
                    onBackToMenuClick = { currentScreen = Screen.HOME },
                    onStartRecording = { currentScreen = Screen.RECORDING }
                )

                Screen.RECORDING -> RecordingScreen(
                    onStopRecording = { currentScreen = Screen.HOME } // Or back to dashboard/sessions
                )

                Screen.SESSIONS -> SessionsScreen(
                    onBackClick = { currentScreen = Screen.HOME },
                    onSessionClick = { session ->
                        selectedSession = session
                        currentScreen = Screen.ANALYSIS
                    }
                )

                Screen.ANALYSIS -> SessionAnalysisScreen(
                    session = selectedSession,
                    onBackClick = { currentScreen = Screen.SESSIONS },
                    onExportClick = { /* Export */ }
                )

                Screen.SETTINGS -> SettingsScreen(
                    onBackClick = { currentScreen = Screen.HOME }
                )
            }
        }
    }
}
