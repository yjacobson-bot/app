package com.ozerli.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Palette ──────────────────────────────────────────────────────────────────
val Indigo900  = Color(0xFF1A1A6E)
val Indigo700  = Color(0xFF2D2DA8)
val Indigo600  = Color(0xFF3D3DBF)
val Indigo500  = Color(0xFF5C5CE0)
val Indigo100  = Color(0xFFE8E8FF)
val Indigo50   = Color(0xFFF4F4FF)

val Surface    = Color(0xFFF7F8FC)
val CardWhite  = Color(0xFFFFFFFF)
val TextPrimary   = Color(0xFF111827)
val TextSecondary = Color(0xFF6B7280)
val TextTertiary  = Color(0xFF9CA3AF)
val Divider    = Color(0xFFE5E7EB)

// Urgency
val RedStrong  = Color(0xFFDC2626)
val RedLight   = Color(0xFFFEF2F2)
val RedBorder  = Color(0xFFFCA5A5)

val AmberStrong = Color(0xFFD97706)
val AmberLight  = Color(0xFFFFFBEB)
val AmberBorder = Color(0xFFFCD34D)

val GreenStrong = Color(0xFF059669)
val GreenLight  = Color(0xFFECFDF5)
val GreenBorder = Color(0xFF6EE7B7)

private val ColorScheme = lightColorScheme(
    primary          = Indigo600,
    onPrimary        = Color.White,
    primaryContainer = Indigo100,
    onPrimaryContainer = Indigo900,
    secondary        = Indigo500,
    background       = Surface,
    surface          = CardWhite,
    surfaceVariant   = Indigo50,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
    outline          = Divider,
)

@Composable
fun OzerLiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        content = content
    )
}
