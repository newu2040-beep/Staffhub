package com.example.ui.theme

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

@Composable
fun StaffHubTheme(
    selectedTheme: AppTheme = AppTheme.OCEAN,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val primary = selectedTheme.primaryColor
    val secondary = selectedTheme.secondaryColor
    val lightBg = selectedTheme.lightBg

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primary.copy(alpha = 0.25f),
            onPrimaryContainer = Color.White,
            secondary = secondary,
            background = Color(0xFF0F172A),
            surface = Color(0xFF1E293B),
            onBackground = Color(0xFFF8FAFC),
            onSurface = Color(0xFFF8FAFC),
            surfaceVariant = Color(0xFF334155),
            onSurfaceVariant = Color(0xFFCBD5E1),
            outline = Color(0xFF475569)
        )
        else -> lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = secondary.copy(alpha = 0.35f),
            onPrimaryContainer = primary,
            secondary = secondary,
            background = lightBg,
            surface = Color(0xFFFFFFFF),
            onBackground = Color(0xFF0F172A),
            onSurface = Color(0xFF0F172A),
            surfaceVariant = secondary.copy(alpha = 0.18f),
            onSurfaceVariant = Color(0xFF475569),
            outline = Color(0xFFE2E8F0)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    StaffHubTheme(
        selectedTheme = AppTheme.OCEAN,
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content = content
    )
}
