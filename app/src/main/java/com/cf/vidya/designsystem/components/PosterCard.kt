package com.cf.vidya.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.cf.vidya.R
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing

/** Rounded series poster with the "👁 750K" view-count badge, used across Home, Library, Profile. */
@Composable
fun PosterCard(
    posterUrl: String,
    contentDescription: String,
    viewCount: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.medium
    Box(
        modifier = modifier
            .clip(shape)
            .background(VidyaColors.Surface)
            .border(1.dp, VidyaColors.Gold, shape)
            .clickable(onClick = onClick),
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
