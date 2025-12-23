package com.haphuongquynh.foodmooddiary.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PastelGreen,              // Xanh lá pastel chính
    secondary = MintGreen,              // Xanh mint phụ
    tertiary = SageGreen,               // Xanh sage
    background = BlackPrimary,          // Nền đen chính
    surface = BlackSecondary,           // Surface đen nhạt
    onPrimary = BlackPrimary,           // Text trên primary
    onSecondary = BlackPrimary,         // Text trên secondary
    onTertiary = BlackPrimary,          // Text trên tertiary
    onBackground = WhiteText,           // Text chính trên nền
    onSurface = WhiteText,              // Text trên surface
    surfaceVariant = BlackTertiary,     // Surface variant
    onSurfaceVariant = PastelGreenLight, // Text trên surface variant
    primaryContainer = PastelGreenDark,  // Container màu chính
    secondaryContainer = BlackTertiary,  // Container phụ
    tertiaryContainer = MintGreen,       // Container tertiary
    error = Color(0xFFCF6679),           // Màu error
    onError = BlackPrimary               // Text trên error
)

private val LightColorScheme = lightColorScheme(
    primary = PastelGreenDark,
    secondary = SageGreen,
    tertiary = MintGreen,
    background = PastelGreenLight,
    surface = WhiteText,
    onPrimary = WhiteText,
    onSecondary = BlackPrimary,
    onTertiary = BlackPrimary,
    onBackground = BlackPrimary,
    onSurface = BlackPrimary,
    surfaceVariant = LimeGreen,
    onSurfaceVariant = BlackSecondary
)

@Composable
fun FoodMoodDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
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