package com.cf.vidya.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// The app is dark-only by design, so there is no light scheme or dynamic colour.
private val VidyaColorScheme = darkColorScheme(
    primary = VidyaColors.Gold,
    onPrimary = VidyaColors.OnGold,
    secondary = VidyaColors.Gold,
    onSecondary = VidyaColors.OnGold,
    background = VidyaColors.Background,
    onBackground = VidyaColors.TextPrimary,
    surface = VidyaColors.Background,
    onSurface = VidyaColors.TextPrimary,
    surfaceVariant = VidyaColors.Surface,
    onSurfaceVariant = VidyaColors.TextSecondary,
    surfaceContainer = VidyaColors.Surface,
    surfaceContainerHigh = VidyaColors.SurfaceElevated,
    outline = VidyaColors.Outline,
    outlineVariant = VidyaColors.Outline,
    error = VidyaColors.Error,
    scrim = VidyaColors.Scrim,
)

private val VidyaShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
)

@Composable
fun VidyaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VidyaColorScheme,
        typography = VidyaTypography,
        shapes = VidyaShapes,
        content = content,
    )
}
