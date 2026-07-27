package com.tracktelemetry.recorder.presentation.dashboard

import android.content.Context
import android.net.Uri
import androidx.camera.video.VideoRecordEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracktelemetry.recorder.data.camera.CameraManager
import com.tracktelemetry.recorder.data.sensors.LocationDataSource
import com.tracktelemetry.recorder.data.sensors.LocationTelemetry
import com.tracktelemetry.recorder.data.sensors.MotionDataSource
import com.tracktelemetry.recorder.data.sensors.MotionTelemetry
import com.tracktelemetry.recorder.data.telemetry.TelemetryCsvWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

data class DashboardUiState(
    val isRecording: Boolean = false,
    val durationSeconds: Long = 0,
    val speedKmh: Float = 0f,
    val gLat: Float = 0f,
    val gLong: Float = 0f,
    val gVert: Float = 0f,
    val gpsAccuracy: Float = 0f,
    val lastRecordedUri: Uri? = null,
    val statusText: String = "READY",
    val errorMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val cameraManager: CameraManager,
    private val locationDataSource: LocationDataSource,
    private val motionDataSource: MotionDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var telemetryJob: Job? = null
    private var csvWriter: TelemetryCsvWriter? = null

    init {
        startTelemetryCollection()
    }

    private fun startTelemetryCollection() {
        telemetryJob = viewModelScope.launch {
            combine(
                locationDataSource.getLocationFlow().catch { emit(LocationTelemetry()) },
                motionDataSource.getMotionFlow().catch { emit(MotionTelemetry()) }
            ) { loc, motion ->
                Pair(loc, motion)
            }.collect { (loc, motion) ->
                _uiState.update {
                    it.copy(
                        speedKmh = loc.speedKmh,
                        gLat = motion.gLat,
                        gLong = motion.gLong,
                        gVert = motion.gVert,
                        gpsAccuracy = loc.accuracy
                    )
                }

                // If currently recording, write telemetry row to CSV
                if (_uiState.value.isRecording) {
                    csvWriter?.writeRow(
                        latitude = loc.latitude,
                        longitude = loc.longitude,
                        speedKmh = loc.speedKmh,
                        altitude = loc.altitude,
                        gLat = motion.gLat,
                        gLong = motion.gLong,
                        gVert = motion.gVert,
                        rawAccelMag = motion.rawAccelMagnitude
                    )
                }
            }
        }
    }

    fun toggleRecording(context: Context) {
        if (_uiState.value.isRecording) {
            stopRecording()
        } else {
            startRecording(context)
        }
    }

    private fun startRecording(context: Context) {
        val fileName = "TRACK_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())

        csvWriter = TelemetryCsvWriter(context).apply {
            startSession(fileName)
        }

        cameraManager.startRecording(context) { event ->
            when (event) {
                is VideoRecordEvent.Start -> {
                    com.tracktelemetry.recorder.service.RecordingForegroundService.startService(context)
                    _uiState.update {
                        it.copy(
                            isRecording = true,
                            durationSeconds = 0,
                            statusText = "RECORDING",
                            errorMessage = null
                        )
                    }
                    startTimer()
                }
                is VideoRecordEvent.Finalize -> {
                    com.tracktelemetry.recorder.service.RecordingForegroundService.stopService(context)
                    stopTimer()
                    csvWriter?.stopSession()
                    csvWriter = null

                    if (event.hasError()) {
                        _uiState.update {
                            it.copy(
                                isRecording = false,
                                statusText = "ERROR",
                                errorMessage = "Recording error: ${event.error}"
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isRecording = false,
                                statusText = "SAVED",
                                lastRecordedUri = event.outputResults.outputUri
                            )
                        }
                    }
                }
            }
        }
    }

    private fun stopRecording() {
        cameraManager.stopRecording()
        stopTimer()
    }

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { it.copy(durationSeconds = it.durationSeconds + 1) }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        if (_uiState.value.isRecording) {
            cameraManager.stopRecording()
        }
        csvWriter?.stopSession()
        telemetryJob?.cancel()
    }
}
