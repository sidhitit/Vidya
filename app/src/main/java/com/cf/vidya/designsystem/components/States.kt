package com.cf.vidya.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cf.vidya.R
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing

/** Pulsing grey block used to build loading skeletons. */
@Composable
fun SkeletonBox(modifier: Modifier = Modifier) {
    val alpha by rememberInfiniteTransition(label = "skeleton").animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "skeletonAlpha",
    )
    Box(
        modifier = modifier
            .graphicsLayer { this.alpha = alpha }
            .clip(MaterialTheme.shapes.medium)
            .background(VidyaColors.SurfaceElevated),
    )
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(VidyaSpacing.xl),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = VidyaColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(VidyaSpacing.l))
        GoldButton(
            text = stringResource(R.string.retry),
            onClick = onRetry,
            modifier = Modifier.width(160.dp),
        )
    }
}

/** Shown when a list has nothing in it yet, with an optional button that leads somewhere useful. */
@Composable
fun EmptyState(
    @DrawableRes icon: Int,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(VidyaSpacing.xl),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = VidyaColors.TextTertiary,
            modifier = Modifier.size(56.dp),
        )
        Spacer(Modifier.height(VidyaSpacing.l))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = VidyaColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(VidyaSpacing.s))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = VidyaColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
        if (actionText != null) {
            Spacer(Modifier.height(VidyaSpacing.xl))
            GoldButton(text = actionText, onClick = onAction, modifier = Modifier.width(200.dp))
        }
    }
}

/** Temporary screen for tabs and pages whose designs aren't ready yet. */
@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .background(VidyaColors.Background)
            .statusBarsPadding(),
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall, color = VidyaColors.TextPrimary)
        Spacer(Modifier.height(VidyaSpacing.s))
        Text(
            text = stringResource(R.string.coming_soon),
            style = MaterialTheme.typography.bodyMedium,
            color = VidyaColors.TextTertiary,
        )
    }
}
