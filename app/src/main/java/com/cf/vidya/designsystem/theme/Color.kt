package com.cf.vidya.designsystem.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Vidya colour tokens.
 *
 * Values are estimated from the XD review link (no specs mode yet). Replace them with the
 * designer's exact hex codes once a specs link or style guide arrives — this is the only file
 * that needs to change.
 */
object VidyaColors {
    val Background = Color(0xFF111111)
    val Surface = Color(0xFF1B1B1B)
    val SurfaceElevated = Color(0xFF262626)
    val Outline = Color(0xFF3A3A3A)
    /** Thin light-grey border around poster cards. */
    val CardOutline = Color(0xFF9E9E9E)

    val Gold = Color(0xFFEFC870)
    val OnGold = Color(0xFF1A1A1A)

    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFB3B3B3)
    val TextTertiary = Color(0xFF7A7A7A)

    val Disabled = Color(0xFF5A5A5A)
    val OnDisabled = Color(0xFFA6A6A6)

    val Error = Color(0xFFFF6B6B)
    val Scrim = Color(0x99000000)

    val PremiumCardOutline = Color(0xFF6E3434)
    val PremiumCard = Brush.linearGradient(listOf(Color(0xFF5C1717), Color(0xFF2A0B0B)))
    val LogoGradient = Brush.linearGradient(listOf(Color(0xFF6A2BD9), Color(0xFF2A0F6B)))
}
