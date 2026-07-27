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
import com.tracktelemetry.recorder.presentation.dashboard.DashboardScreen
import com.tracktelemetry.recorder.presentation.home.HomeScreen
import com.tracktelemetry.recorder.presentation.theme.TrackTelemetryRecorderTheme
import dagger.hilt.android.AndroidEntryPoint

enum class Screen {
    HOME,
    DASHBOARD,
    SETTINGS,
    HISTORY
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

    when (currentScreen) {
        Screen.HOME -> HomeScreen(
            onStartTrackClick = { currentScreen = Screen.DASHBOARD },
            onSettingsClick = { currentScreen = Screen.SETTINGS },
            onHistoryClick = { currentScreen = Screen.HISTORY }
        )
        Screen.DASHBOARD -> DashboardScreen(
            onBackToMenuClick = { currentScreen = Screen.HOME }
        )
        Screen.SETTINGS -> {
            com.tracktelemetry.recorder.presentation.settings.SettingsScreen(
                onBackClick = { currentScreen = Screen.HOME }
            )
        }
        Screen.HISTORY -> {
            // Placeholder for History
            HomeScreen(
                onStartTrackClick = { currentScreen = Screen.DASHBOARD },
                onSettingsClick = { currentScreen = Screen.SETTINGS },
                onHistoryClick = { currentScreen = Screen.HISTORY }
            )
        }
    }
}
