package com.tracktelemetry.recorder.data.telemetry

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.SystemClock
import android.provider.MediaStore
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.util.Locale

class TelemetryCsvWriter(
    private val context: Context
) {
    private var outputStream: OutputStream? = null
    private var writer: OutputStreamWriter? = null
    private var sessionStartElapsedMs: Long = 0L

    fun startSession(fileName: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.csv")
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/TrackTelemetry")
        }

        val uri = context.contentResolver.insert(
            MediaStore.Files.getContentUri("external"),
            contentValues
        )

        uri?.let {
            outputStream = context.contentResolver.openOutputStream(it)
            writer = OutputStreamWriter(outputStream, Charsets.UTF_8)

            sessionStartElapsedMs = SystemClock.elapsedRealtime()

            // Header matching Section 4.3 of SRS
            val header = "timestamp_offset_ms,elapsed_realtime_ns,latitude,longitude,speed_kmh,altitude,g_lat,g_long,g_vert,acc_m_s2\n"
            writer?.write(header)
            writer?.flush()
        }

        return uri
    }

    fun writeRow(
        latitude: Double,
        longitude: Double,
        speedKmh: Float,
        altitude: Double,
        gLat: Float,
        gLong: Float,
        gVert: Float,
        rawAccelMag: Float
    ) {
        val currentElapsed = SystemClock.elapsedRealtime()
        val offsetMs = currentElapsed - sessionStartElapsedMs
        val elapsedNs = SystemClock.elapsedRealtimeNanos()

        val row = String.format(
            Locale.US,
            "%d,%d,%.6f,%.6f,%.2f,%.2f,%.3f,%.3f,%.3f,%.3f\n",
            offsetMs,
            elapsedNs,
            latitude,
            longitude,
            speedKmh,
            altitude,
            gLat,
            gLong,
            gVert,
            rawAccelMag
        )

        writer?.write(row)
        writer?.flush()
    }

    fun stopSession() {
        try {
            writer?.flush()
            writer?.close()
            outputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            writer = null
            outputStream = null
        }
    }
}
