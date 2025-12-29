package com.example.myaccidentapplication.service

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class AccidentSensorManager(
    private val sensorManager: SensorManager,
    private val onAccidentDetected: (Float, Float, Float, Float, Float, Float) -> Unit
) : SensorEventListener {

    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    // Store latest values
    private var lastAx = 0f
    private var lastAy = 0f
    private var lastAz = 0f

    private var lastGx = 0f
    private var lastGy = 0f
    private var lastGz = 0f

    fun startMonitoring() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stopMonitoring() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {

            Sensor.TYPE_ACCELEROMETER -> {
                lastAx = event.values[0]
                lastAy = event.values[1]
                lastAz = event.values[2]

                // 🚨 Accident threshold (adjust if needed)
                if (kotlin.math.abs(lastAx) > 30 ||
                    kotlin.math.abs(lastAy) > 30 ||
                    kotlin.math.abs(lastAz) > 30) {

                    // Now we send ALL 6 VALUES
                    onAccidentDetected(lastAx, lastAy, lastAz, lastGx, lastGy, lastGz)
                }
            }

            Sensor.TYPE_GYROSCOPE -> {
                lastGx = event.values[0]
                lastGy = event.values[1]
                lastGz = event.values[2]
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
