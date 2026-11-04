package com.cf.vidya.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.cf.vidya.R
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing
import com.cf.vidya.designsystem.theme.VidyaTheme

/**
 * Rounded series poster with the "👁 750K" view-count badge, used across Home, Library, Profile.
 * Pass [progress] (0 to 1) to show how much has been watched as a gold bar along the bottom.
 */
@Composable
fun PosterCard(
    posterUrl: String,
    contentDescription: String,
    viewCount: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    progress: Float? = null,
    /** Pass one in to react to presses from outside, e.g. to scale the card while it's held. */
    interactionSource: MutableInteractionSource? = null,
) {
    val shape = MaterialTheme.shapes.medium
    Box(
        modifier = modifier
            .clip(shape)
            .background(VidyaColors.Surface)
            .border(1.dp, VidyaColors.CardOutline, shape)
            .clickable(interactionSource = interactionSource, indication = ripple(), onClick = onClick),
    ) {
        AsyncImage(
            model = posterUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        ViewCountBadge(
            count = viewCount,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(VidyaSpacing.s),
        )
        if (progress != null) {
            WatchProgressBar(
                progress = progress,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

/** Thin bar: a dark track across the full width, filled in gold up to [progress]. */
@Composable
private fun WatchProgressBar(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(5.dp)
            // Dark track stays visible on bright posters too.
            .background(Color.Black.copy(alpha = 0.6f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(VidyaColors.Gold),
        )
    }
}

@Composable
fun ViewCountBadge(count: Long, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(VidyaColors.Scrim)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_eye),
            contentDescription = stringResource(R.string.views),
            tint = VidyaColors.TextPrimary,
            modifier = Modifier.size(12.dp),
        )
        Spacer(Modifier.width(VidyaSpacing.xs))
        Text(
            text = count.toCompactCount(),
            style = MaterialTheme.typography.labelSmall,
            color = VidyaColors.TextPrimary,
        )
    }
}

/** 750000 → "750K", 1200000 → "1.2M". */
fun Long.toCompactCount(): String = when {
    this >= 1_000_000 -> (this / 100_000).toOneDecimal() + "M"
    this >= 1_000 -> (this / 100).toOneDecimal() + "K"
    else -> toString()
}

// Receives a value in tenths (e.g. 12 → "1.2", 7500 → "750").
private fun Long.toOneDecimal(): String = if (this % 10 == 0L) "${this / 10}" else "${this / 10}.${this % 10}"

@Preview
@Composable
private fun PosterCardPreview() {
    VidyaTheme {
        PosterCard(
            posterUrl = "https://picsum.photos/seed/vidya-1/400/600",
            contentDescription = "Poster description",
            viewCount = 750000L,
            onClick = {},
            modifier = Modifier
                .width(120.dp)
                .height(160.dp),
        )
    }
}

@Preview
@Composable
private fun PosterCardWithProgressPreview() {
    VidyaTheme {
        PosterCard(
            posterUrl = "https://picsum.photos/seed/vidya-1/400/600",
            contentDescription = "Poster description",
            viewCount = 1200000L,
            progress = 0.65f,
            onClick = {},
            modifier = Modifier
                .width(120.dp)
                .height(160.dp),
        )
    }
}
