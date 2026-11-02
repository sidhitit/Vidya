package com.cf.vidya.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing

/**
 * Shared frame of the Login and OTP screens: tilted poster collage on top,
 * logo and the screen's [content] at the bottom. The content moves up with the keyboard.
 */
@Composable
fun AuthLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VidyaColors.Background),
    ) {
        PosterCollage(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.62f),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                // Keeps the text readable when the keyboard pushes the form up over the posters.
                .background(Brush.verticalGradient(0f to Color.Transparent, 0.2f to VidyaColors.Background))
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = VidyaSpacing.xl, vertical = VidyaSpacing.xl),
        ) {
            VidyaAppIcon()
            Spacer(Modifier.height(VidyaSpacing.m))
            content()
        }
    }
}

private const val CollageRows = 5
private const val CollageColumns = 6

@Composable
private fun PosterCollage(modifier: Modifier = Modifier) {
    Box(modifier = modifier.clipToBounds()) {
        // Wider and taller than the screen, then rotated, so the tilted grid has no empty corners.
        Column(
            verticalArrangement = Arrangement.spacedBy(VidyaSpacing.s),
            modifier = Modifier
                .align(Alignment.Center)
                .requiredSize(width = 660.dp, height = 760.dp)
                .graphicsLayer { rotationZ = -14f },
        ) {
            repeat(CollageRows) { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(VidyaSpacing.s),
                    // Every other row is shifted so the posters look staggered.
                    modifier = Modifier.offset(x = if (row % 2 == 1) (-50).dp else 0.dp),
                ) {
                    repeat(CollageColumns) { column ->
                        AsyncImage(
                            // Placeholder art until the real posters are available.
                            model = "https://picsum.photos/seed/vidya-auth-${row * CollageColumns + column}/240/340",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(width = 100.dp, height = 144.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(VidyaColors.Surface),
                        )
                    }
                }
            }
        }
        // Fades the collage into the black background.
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to VidyaColors.Background.copy(alpha = 0.35f),
                        1f to VidyaColors.Background,
                    ),
                ),
        )
    }
}

/** Placeholder for the Vidya app icon until the designer shares the SVG. */
@Composable
private fun VidyaAppIcon(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .size(76.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(VidyaColors.LogoGradient),
    ) {
        Text(
            text = "V",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = VidyaColors.Gold,
        )
        Text(
            text = "VIDYA",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = Color.White,
        )
    }
}
