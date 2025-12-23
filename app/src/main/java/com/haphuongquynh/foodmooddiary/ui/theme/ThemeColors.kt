package com.haphuongquynh.foodmooddiary.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * Extended colors for theme consistency
 * Based on Pastel Green & Black theme
 */
object ThemeColors {
    // Pastel Green variants
    val pastelGreen: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
    val pastelGreenLight: Color @Composable @ReadOnlyComposable get() = PastelGreenLight
    val pastelGreenDark: Color @Composable @ReadOnlyComposable get() = PastelGreenDark
    val mintGreen: Color @Composable @ReadOnlyComposable get() = MintGreen
    val sageGreen: Color @Composable @ReadOnlyComposable get() = SageGreen
    val limeGreen: Color @Composable @ReadOnlyComposable get() = LimeGreen
    
    // Black & Dark variants
    val blackPrimary: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background
    val blackSecondary: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface
    val blackTertiary: Color @Composable @ReadOnlyComposable get() = BlackTertiary
    val charcoalGray: Color @Composable @ReadOnlyComposable get() = CharcoalGray
    
    // Accent & Special colors
    val greenAccent: Color @Composable @ReadOnlyComposable get() = PastelGreenDark
    val whiteText: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onBackground
    
    // Legacy color replacements (for gradual migration)
    val oldDarkBackground: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background
    val oldDarkSurface: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface
    val oldGoldAccent: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
}
