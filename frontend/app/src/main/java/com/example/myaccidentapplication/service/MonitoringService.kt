package com.example.myaccidentapplication.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.myaccidentapplication.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MonitoringService : Service() {

    private var sensorManager: SensorManager? = null
    private var accidentSensorManager: AccidentSensorManager? = null
    private var vibrator: Vibrator? = null

    companion object {
        private const val CHANNEL_ID = "MonitoringServiceChannel"
        private const val NOTIFICATION_ID = 1
        const val ACTION_ACCIDENT_DETECTED = "com.example.myaccidentapplication.ACCIDENT_DETECTED"
        const val EXTRA_ACCEL_X = "accel_x"
        const val EXTRA_ACCEL_Y = "accel_y"
        const val EXTRA_ACCEL_Z = "accel_z"
        const val EXTRA_GYRO_X = "gyro_x"
        const val EXTRA_GYRO_Y = "gyro_y"
        const val EXTRA_GYRO_Z = "gyro_z"
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Initialize vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMonitoring()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Accident Monitoring Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors for accidents using sensors"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Accident Monitoring Active")
            .setContentText("Monitoring for accidents...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun startMonitoring() {
        sensorManager?.let { sm ->
            accidentSensorManager = AccidentSensorManager(sm) { accelX, accelY, accelZ, gyroX, gyroY, gyroZ ->
                // ✅ Run detection callback off the main thread
                CoroutineScope(Dispatchers.IO).launch {
                    onAccidentDetected(accelX, accelY, accelZ, gyroX, gyroY, gyroZ)
                }
            }
            accidentSensorManager?.startMonitoring()
        }
    }

    private fun onAccidentDetected(
        accelX: Float,
        accelY: Float,
        accelZ: Float,
        gyroX: Float,
        gyroY: Float,
        gyroZ: Float
    ) {
        // Vibrate immediately (safe to run in IO)
        vibrate()

        // Broadcast accident detection
        val intent = Intent(ACTION_ACCIDENT_DETECTED).apply {
            putExtra(EXTRA_ACCEL_X, accelX)
            putExtra(EXTRA_ACCEL_Y, accelY)
            putExtra(EXTRA_ACCEL_Z, accelZ)
            putExtra(EXTRA_GYRO_X, gyroX)
            putExtra(EXTRA_GYRO_Y, gyroY)
            putExtra(EXTRA_GYRO_Z, gyroZ)
        }
        sendBroadcast(intent)
    }

    private fun vibrate() {
        vibrator?.let { v ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(
                    VibrationEffect.createOneShot(
                        1000,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(1000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        accidentSensorManager?.stopMonitoring()
    }
}