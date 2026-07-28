package com.tracktelemetry.recorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tracktelemetry.recorder.presentation.analysis.SessionAnalysisScreen
import com.tracktelemetry.recorder.presentation.dashboard.DashboardScreen
import com.tracktelemetry.recorder.presentation.history.SessionSummary
import com.tracktelemetry.recorder.presentation.history.SessionsScreen
import com.tracktelemetry.recorder.presentation.home.HomeScreen
import com.tracktelemetry.recorder.presentation.recording.RecordingScreen
import com.tracktelemetry.recorder.presentation.settings.SettingsScreen
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
                    color = MaterialTheme.colorScheme.background
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

    when (currentScreen) {

        Screen.HOME -> HomeScreen(
            onStartTrackClick = { currentScreen = Screen.DASHBOARD },
            onSettingsClick = { currentScreen = Screen.SETTINGS },
            onHistoryClick = { currentScreen = Screen.SESSIONS },
            onCameraClick = { currentScreen = Screen.DASHBOARD }
        )

        Screen.DASHBOARD -> DashboardScreen(
            onBackToMenuClick = { currentScreen = Screen.HOME }
        )

        Screen.RECORDING -> RecordingScreen(
            onBackClick = { currentScreen = Screen.HOME },
            onToggleRecord = { /* handled inside RecordingScreen */ }
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
            onExportClick = { /* Phase 4: Export */ }
        )

        Screen.SETTINGS -> SettingsScreen(
            onBackClick = { currentScreen = Screen.HOME }
        )
    }
}
