package com.ozerli.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val GreenPrimary = Color(0xFF2E7D32)
val GreenLight = Color(0xFF4CAF50)
val GreenContainer = Color(0xFFE8F5E9)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenPrimary,
    secondary = Color(0xFF558B2F),
    background = Color(0xFFF9FBF9),
    surface = Color.White,
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A),
)

@Composable
fun OzerLiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}

// צבעי דחיפות
val UrgencyRed = Color(0xFFD32F2F)
val UrgencyRedLight = Color(0xFFFFEBEE)
val UrgencyYellow = Color(0xFFF57C00)
val UrgencyYellowLight = Color(0xFFFFF3E0)
val UrgencyGreen = Color(0xFF388E3C)
val UrgencyGreenLight = Color(0xFFE8F5E9)
