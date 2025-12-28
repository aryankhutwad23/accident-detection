package com.example.myaccidentapplication.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DeepBlue80,
    secondary = Teal80,
    tertiary = Amber80,
    background = DeepBlue,
    surface = DeepBlue40,
    onPrimary = White,
    onSecondary = White,
    onTertiary = DeepBlue,
    onBackground = White,
    onSurface = White,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = DeepBlue,
    secondary = Teal,
    tertiary = Amber,
    background = White,
    surface = Color(0xFFF5F5F5),
    onPrimary = White,
    onSecondary = White,
    onTertiary = DeepBlue,
    onBackground = DeepBlue,
    onSurface = DeepBlue,
    error = ErrorRed
)

@Composable
fun MyAccidentApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Disable dynamic color to use custom palette
        false && dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}