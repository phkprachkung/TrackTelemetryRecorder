package com.tracktelemetry.recorder.data.camera

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraManager @Inject constructor() {

    private var videoCapture: VideoCapture<Recorder>? = null
    private var activeRecording: Recording? = null
    private var mainExecutor: Executor? = null

    fun bindCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onCameraBound: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        mainExecutor = ContextCompat.getMainExecutor(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                val qualitySelector = QualitySelector.fromOrderedList(
                    listOf(Quality.FHD, Quality.HD, Quality.SD, Quality.LOWEST),
                    FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
                )

                val recorder = Recorder.Builder()
                    .setQualitySelector(qualitySelector)
                    .build()

                videoCapture = VideoCapture.withOutput(recorder)

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    videoCapture
                )

                onCameraBound()
            } catch (e: Exception) {
                onError(e)
            }
        }, mainExecutor!!)
    }

    fun startRecording(
        context: Context,
        onEvent: (VideoRecordEvent) -> Unit
    ) {
        val capture = videoCapture ?: return
        val name = "TRACK_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())

        val executor = mainExecutor ?: ContextCompat.getMainExecutor(context)

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/TrackTelemetry")
            }

            val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(
                context.contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            ).setContentValues(contentValues).build()

            val pendingRecording = capture.output.prepareRecording(context, mediaStoreOutputOptions)

            val hasAudioPermission = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.RECORD_AUDIO
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (hasAudioPermission) {
                try {
                    pendingRecording.withAudioEnabled()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            activeRecording = pendingRecording.start(executor) { event ->
                onEvent(event)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback for emulator / restricted storage environments
            try {
                val outputFile = File(context.externalCacheDir ?: context.cacheDir, "$name.mp4")
                val fileOutputOptions = FileOutputOptions.Builder(outputFile).build()
                val pendingRecording = capture.output.prepareRecording(context, fileOutputOptions)

                activeRecording = pendingRecording.start(executor) { event ->
                    onEvent(event)
                }
            } catch (fallbackException: Exception) {
                fallbackException.printStackTrace()
            }
        }
    }

    fun stopRecording() {
        try {
            activeRecording?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            activeRecording = null
        }
    }

    fun isRecording(): Boolean = activeRecording != null
}
