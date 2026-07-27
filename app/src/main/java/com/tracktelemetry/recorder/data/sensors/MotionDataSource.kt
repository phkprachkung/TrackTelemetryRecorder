package com.tracktelemetry.recorder.data.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

data class MotionTelemetry(
    val gLat: Float = 0f,      // Cornering G (Lateral)
    val gLong: Float = 0f,     // Accel / Brake G (Longitudinal)
    val gVert: Float = 0f,     // Vertical G
    val rawAccelMagnitude: Float = 0f,
    val timestampNs: Long = System.nanoTime()
)

@Singleton
class MotionDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun getMotionFlow(): Flow<MotionTelemetry> = callbackFlow {
        val linearAccelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (linearAccelSensor == null) {
            close(IllegalStateException("No accelerometer sensor available on device"))
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // Convert m/s^2 to G-force (1G ~ 9.80665 m/s^2)
                val gFactor = 9.80665f
                val gLat = event.values[0] / gFactor
                val gLong = event.values[1] / gFactor
                val gVert = event.values[2] / gFactor

                val mag = kotlin.math.sqrt(
                    event.values[0] * event.values[0] +
                    event.values[1] * event.values[1] +
                    event.values[2] * event.values[2]
                )

                trySend(
                    MotionTelemetry(
                        gLat = gLat,
                        gLong = gLong,
                        gVert = gVert,
                        rawAccelMagnitude = mag,
                        timestampNs = event.timestamp
                    )
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(
            listener,
            linearAccelSensor,
            SensorManager.SENSOR_DELAY_GAME
        )

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
