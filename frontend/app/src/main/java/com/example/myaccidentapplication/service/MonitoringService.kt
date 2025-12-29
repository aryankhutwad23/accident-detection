package com.example.myaccidentapplication.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.*
import androidx.core.app.NotificationCompat
import com.example.myaccidentapplication.ui.MainActivity
import kotlinx.coroutines.*

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

        // Vibrator init safe for all versions
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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
                CHANNEL_ID, "Accident Monitoring Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Accident Monitoring Active")
            .setContentText("Monitoring sensors...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun startMonitoring() {
        sensorManager?.let { sm ->
            accidentSensorManager = AccidentSensorManager(sm) { ax, ay, az, gx, gy, gz ->
                CoroutineScope(Dispatchers.IO).launch {
                    onAccidentDetected(ax, ay, az, gx, gy, gz)
                }
            }
            accidentSensorManager?.startMonitoring()
        }
    }

    private fun onAccidentDetected(
        ax: Float, ay: Float, az: Float,
        gx: Float, gy: Float, gz: Float
    ) {
        vibratePhone()

        val intent = Intent(ACTION_ACCIDENT_DETECTED).apply {
            putExtra(EXTRA_ACCEL_X, ax)
            putExtra(EXTRA_ACCEL_Y, ay)
            putExtra(EXTRA_ACCEL_Z, az)
            putExtra(EXTRA_GYRO_X, gx)
            putExtra(EXTRA_GYRO_Y, gy)
            putExtra(EXTRA_GYRO_Z, gz)
        }
        sendBroadcast(intent)
    }

    private fun vibratePhone() {
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(800)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        accidentSensorManager?.stopMonitoring()
    }
}
