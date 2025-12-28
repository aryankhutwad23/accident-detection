package com.example.myaccidentapplication.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myaccidentapplication.ui.theme.Amber
import com.example.myaccidentapplication.ui.theme.DeepBlue
import com.example.myaccidentapplication.ui.theme.ErrorRed
import com.example.myaccidentapplication.ui.theme.Teal
import com.example.myaccidentapplication.util.LocationManager
import kotlinx.coroutines.delay

@Composable
fun AccidentAlertScreen(
    onSafe: () -> Unit,
    onNeedHelp: (lat: Double?, lon: Double?) -> Unit,
    onTimeout: (lat: Double?, lon: Double?) -> Unit
) {
    val context = LocalContext.current
    var timeRemaining by remember { mutableStateOf(30) }
    var locationText by remember { mutableStateOf("Getting location...") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var hasResponded by remember { mutableStateOf(false) }

    // Vibrate on first composition
    LaunchedEffect(Unit) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    1000,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(1000)
        }
    }

    // Get location safely
    LaunchedEffect(Unit) {
        val locationManager = LocationManager(context)
        val location = locationManager.getLastLocationOrNull()   // ✅ FIXED
        if (location != null) {
            latitude = location.latitude
            longitude = location.longitude
            locationText =
                "Lat: ${String.format("%.6f", location.latitude)}, Lng: ${String.format("%.6f", location.longitude)}"
        } else {
            locationText = "Location unavailable"
        }
    }

    // Countdown timer
    LaunchedEffect(Unit) {
        while (timeRemaining > 0 && !hasResponded) {
            delay(1000)
            timeRemaining--
        }
        if (!hasResponded && timeRemaining == 0) {
            onTimeout(latitude, longitude)   // ✅ Pass GPS to timeout
        }
    }

    // Pulsing animation for alert
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ErrorRed.copy(alpha = 0.9f),
                        DeepBlue.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🚨", fontSize = 80.sp, modifier = Modifier.padding(bottom = 16.dp))

            Text(
                text = "Accident Detected!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Are you safe?",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Countdown Timer
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Amber.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Auto-alert in:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "${timeRemaining}s",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Location Display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Teal.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📍 Location",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = locationText,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        hasResponded = true
                        onSafe()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal)
                ) {
                    Text("Yes, I'm Safe", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        hasResponded = true
                        onNeedHelp(latitude, longitude)   // ✅ Pass GPS to help
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Need Help", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "If no response, emergency alert will be sent automatically",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}