package com.example.myaccidentapplication.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.myaccidentapplication.di.AppContainer
import com.example.myaccidentapplication.service.MonitoringService
import com.example.myaccidentapplication.ui.navigation.AppNavigation
import com.example.myaccidentapplication.ui.navigation.Screen
import com.example.myaccidentapplication.ui.theme.MyAccidentApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer
    private val accidentDetectedState = mutableStateOf(false)

    private val accidentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == MonitoringService.ACTION_ACCIDENT_DETECTED) {
                accidentDetectedState.value = true
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            startMonitoringService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appContainer = AppContainer(applicationContext)

        setContent {
            MyAccidentApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val accidentDetected = accidentDetectedState.value

                    LaunchedEffect(accidentDetected) {
                        if (accidentDetected) {
                            navController.navigate(Screen.AccidentAlert.route)
                            accidentDetectedState.value = false
                        }
                    }

                    AppNavigation(
                        navController = navController,
                        appContainer = appContainer
                    )
                }
            }
        }

        requestPermissions()
        registerAccidentReceiver()
    }

    override fun onResume() {
        super.onResume()
        if (hasAllPermissions()) {
            startMonitoringService()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(accidentReceiver)
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>().apply {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS) // ✅ only request on API 33+
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            startMonitoringService()
        }
    }

    private fun hasAllPermissions(): Boolean {
        val requiredPermissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startMonitoringService() {
        if (hasAllPermissions()) {
            val serviceIntent = Intent(this, MonitoringService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
    }

    private fun registerAccidentReceiver() {
        val filter = IntentFilter(MonitoringService.ACTION_ACCIDENT_DETECTED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(accidentReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(accidentReceiver, filter)
        }
    }
}