package com.example.myaccidentapplication.service

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class AccidentSensorManager(
    private val sensorManager: SensorManager,
    private val onAccidentDetected: (Float, Float, Float, Float, Float, Float) -> Unit
) : SensorEventListener {

    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    // Thresholds for accident detection
    private val ACCELERATION_THRESHOLD = 15.0f // m/s²
    private val GYROSCOPE_THRESHOLD = 10.0f // rad/s

    private var lastAccelerometerValues = FloatArray(3)
    private var lastGyroscopeValues = FloatArray(3)

    fun startMonitoring() {
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        accelerometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        gyroscope?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    fun stopMonitoring() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]

                    // Calculate magnitude of acceleration
                    val magnitude = sqrt(x * x + y * y + z * z)

                    // Check for sudden change (impact detection)
                    val deltaX = kotlin.math.abs(x - lastAccelerometerValues[0])
                    val deltaY = kotlin.math.abs(y - lastAccelerometerValues[1])
                    val deltaZ = kotlin.math.abs(z - lastAccelerometerValues[2])
                    val deltaMagnitude = sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)

                    if (magnitude > ACCELERATION_THRESHOLD || deltaMagnitude > ACCELERATION_THRESHOLD) {
                        // Potential accident detected
                        checkForAccident(
                            accelX = x,
                            accelY = y,
                            accelZ = z,
                            gyroX = lastGyroscopeValues[0],
                            gyroY = lastGyroscopeValues[1],
                            gyroZ = lastGyroscopeValues[2]
                        )
                    }

                    lastAccelerometerValues[0] = x
                    lastAccelerometerValues[1] = y
                    lastAccelerometerValues[2] = z
                }

                Sensor.TYPE_GYROSCOPE -> {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]

                    // Calculate magnitude of angular velocity
                    val magnitude = sqrt(x * x + y * y + z * z)

                    if (magnitude > GYROSCOPE_THRESHOLD) {
                        // Potential accident detected
                        checkForAccident(
                            accelX = lastAccelerometerValues[0],
                            accelY = lastAccelerometerValues[1],
                            accelZ = lastAccelerometerValues[2],
                            gyroX = x,
                            gyroY = y,
                            gyroZ = z
                        )
                    }

                    lastGyroscopeValues[0] = x
                    lastGyroscopeValues[1] = y
                    lastGyroscopeValues[2] = z
                }
            }
        }
    }

    private fun checkForAccident(
        accelX: Float,
        accelY: Float,
        accelZ: Float,
        gyroX: Float,
        gyroY: Float,
        gyroZ: Float
    ) {
        // Trigger accident detection callback
        onAccidentDetected(accelX, accelY, accelZ, gyroX, gyroY, gyroZ)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }
}

