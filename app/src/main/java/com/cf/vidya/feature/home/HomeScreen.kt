package com.cf.vidya.feature.home

import android.provider.Settings
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.cf.vidya.R
import com.cf.vidya.designsystem.components.ErrorState
import com.cf.vidya.designsystem.components.PosterCard
import com.cf.vidya.designsystem.components.SectionTitle
import com.cf.vidya.designsystem.components.SkeletonBox
import com.cf.vidya.designsystem.components.VidyaTopBar
import com.cf.vidya.designsystem.motion.TiltState
import com.cf.vidya.designsystem.motion.rememberTiltState
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing
import com.cf.vidya.designsystem.theme.VidyaTheme
import com.cf.vidya.feature.common.messageRes
import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.domain.model.ContinueWatching
import com.cf.vidya.shared.domain.model.Series
import com.cf.vidya.shared.presentation.home.HomeEvent
import com.cf.vidya.shared.presentation.home.HomeState
import kotlin.math.absoluteValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop

/** Stateless Home UI: draws [state] and reports user actions through [onEvent]. */
@Composable
fun HomeScreen(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VidyaColors.Background),
    ) {
        VidyaTopBar(
            onSearchClick = { onEvent(HomeEvent.SearchClicked) },
            onProfileClick = { onEvent(HomeEvent.ProfileClicked) },
        )
        val error = state.error
        when {
            state.isLoading -> HomeSkeleton()
            error != null -> ErrorState(
                message = stringResource(error.messageRes()),
                onRetry = { onEvent(HomeEvent.RetryClicked) },
            )
            else -> HomeContent(state = state, onEvent = onEvent)
        }
    }
}

@Composable
private fun HomeContent(state: HomeState, onEvent: (HomeEvent) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(top = VidyaSpacing.s, bottom = VidyaSpacing.bottomBarClearance),
        modifier = Modifier.fillMaxSize(),
    ) {
        if (state.featured.isNotEmpty()) {
            item(key = "hero") {
                HeroCarousel(
                    series = state.featured,
                    onSeriesClick = { onEvent(HomeEvent.SeriesClicked(it)) },
                )
            }
        }
        if (state.continueWatching.isNotEmpty()) {
            item(key = "continue_watching") {
                ContinueWatchingRow(
                    items = state.continueWatching,
                    onSeriesClick = { onEvent(HomeEvent.SeriesClicked(it)) },
                )
            }
        }
    }
}

/** Height of the centred poster; the page is taller so its shadow has room. */
private val HeroCardHeight = 320.dp

/** How long each card stays in the centre before the carousel moves on by itself. */
private const val AutoSlideIntervalMs = 4_000L

/** Slower and smoother than a fling, so the auto-slide feels calm rather than jumpy. */
private val AutoSlideSpec = tween<Float>(durationMillis = 700, easing = FastOutSlowInEasing)

@Composable
private fun HeroCarousel(series: List<Series>, onSeriesClick: (String) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { series.size })
    val haptics = LocalHapticFeedback.current
    val tilt = rememberTiltState()
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    var isAutoSliding by remember { mutableStateOf(false) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    // Respect "Remove animations" in the phone's accessibility settings.
    val animationsEnabled = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) != 0f
    }

    // A light tick each time the user swipes a new card into the centre, like a dial clicking into place.
    // Not for auto-slides: a buzz every few seconds without touching the phone would be annoying.
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .drop(1) // not for the first page shown
            .collect { if (!isAutoSliding) haptics.performHapticFeedback(HapticFeedbackType.SegmentTick) }
    }

    // Moves on to the next card every few seconds, and back to the first after the last.
    // A swipe restarts the wait (this effect restarts when isDragged changes),
    // and it stops while the app is in the background.
    LaunchedEffect(pagerState, isDragged, series.size) {
        if (isDragged || series.size < 2 || !animationsEnabled) return@LaunchedEffect
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) {
                delay(AutoSlideIntervalMs)
                isAutoSliding = true
                try {
                    pagerState.animateScrollToPage(
                        page = (pagerState.currentPage + 1) % series.size,
                        animationSpec = AutoSlideSpec,
                    )
                } finally {
                    isAutoSliding = false
                }
            }
        }
    }

    Box {
        AmbientGlow(series = series, pagerState = pagerState, tilt = tilt, modifier = Modifier.matchParentSize())
        Column {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 56.dp),
                pageSpacing = VidyaSpacing.m,
                key = { series[it].id },
            ) { page ->
                HeroCard(
                    series = series[page],
                    pagerState = pagerState,
                    page = page,
                    tilt = tilt,
                    onClick = { onSeriesClick(series[page].id) },
                )
            }
            PagerDots(count = series.size, current = pagerState.currentPage)
            Spacer(Modifier.height(VidyaSpacing.s))
        }
    }
}

/** How blurred a card is when it's a full card away from the centre. */
private val MaxCardBlur = 14.dp

// Tilt parallax: layers move by different amounts when the phone tilts, which reads as depth.
/** How far the centred card turns towards the tilt. */
private const val TiltCardDegrees = 5f
/** How far the backlight behind the card slides (opposite way to the card). */
private val TiltLightShift = 18.dp
/** How far the blurred glow behind the whole carousel slides — the deepest layer moves most. */
private val TiltGlowShift = 24.dp

@Composable
private fun HeroCard(
    series: Series,
    pagerState: PagerState,
    page: Int,
    tilt: TiltState,
    onClick: () -> Unit,
) {
    // "Pop": the card sinks a little while your finger is on it and springs back on release.
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "pressScale",
    )
    val shape = MaterialTheme.shapes.medium

    // All effects below are read inside graphicsLayer/drawBehind, so they update every frame
    // of the swipe without recomposing the card.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = VidyaSpacing.l)
            .graphicsLayer {
                // -1 = one card to the left of centre, 0 = centred, 1 = one card to the right.
                val offset = pagerState.signedOffsetFor(page).coerceIn(-1f, 1f)
                val distance = offset.absoluteValue
                // Side cards turn towards the centre, shrink and fade, so the centred one stands out.
                // The centred card also turns slightly with the phone's tilt; side cards stay still.
                val focus = 1f - distance
                rotationY = offset * 18f + tilt.x * TiltCardDegrees * focus
                rotationX = -tilt.y * TiltCardDegrees * focus
                cameraDistance = 12f * density
                val scale = lerp(1f, 0.82f, distance) * pressScale
                scaleX = scale
                scaleY = scale
                alpha = lerp(1f, 0.6f, distance)
            },
    ) {
        // Light behind the centred card: a warm spotlight that makes it look lit from behind.
        // Fades out as the card moves away from the centre.
        Box(
            modifier = Modifier
                .matchParentSize()
                .drawBehind {
                    val focus = 1f - pagerState.signedOffsetFor(page).absoluteValue.coerceIn(0f, 1f)
                    if (focus <= 0f) return@drawBehind
                    // The light sits "behind" the card, so it slides the opposite way to the tilt.
                    val lightCenter = center - Offset(tilt.x, tilt.y) * (TiltLightShift.toPx() * focus)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(VidyaColors.Gold.copy(alpha = 0.32f * focus), Color.Transparent),
                            center = lightCenter,
                            radius = size.maxDimension * 0.75f,
                        ),
                        radius = size.maxDimension * 0.75f,
                        center = lightCenter,
                    )
                },
        )
        Box(
            modifier = Modifier.graphicsLayer {
                // Blur grows with the distance from the centre: the card you swipe away
                // blurs out while the incoming one comes into focus. (Android 12+.)
                val distance = pagerState.signedOffsetFor(page).absoluteValue.coerceIn(0f, 1f)
                val radius = MaxCardBlur.toPx() * distance
                renderEffect = if (radius > 0.5f) BlurEffect(radius, radius, TileMode.Decal) else null
            },
        ) {
            val distance = pagerState.signedOffsetFor(page).absoluteValue.coerceIn(0f, 1f)
            PosterCard(
                posterUrl = series.posterUrl,
                contentDescription = series.title,
                viewCount = series.viewCount,
                onClick = onClick,
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HeroCardHeight)
                    // Deep shadow with a gold tint under the centred card; flattens as it moves aside.
                    .shadow(
                        elevation = lerp(28.dp, 4.dp, distance),
                        shape = shape,
                        ambientColor = Color.Black,
                        spotColor = VidyaColors.Gold,
                    ),
            )
            // Sheen: a soft diagonal highlight across the top of the centred card, like light on glass.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
                    .drawBehind {
                        val focus = 1f - pagerState.signedOffsetFor(page).absoluteValue.coerceIn(0f, 1f)
                        // The highlight glides across the card as you tilt, like light moving on glass.
                        val glide = Offset(tilt.x * size.width, tilt.y * size.height) * (0.4f * focus)
                        drawRect(
                            Brush.linearGradient(
                                0f to Color.White.copy(alpha = 0.18f * focus),
                                0.35f to Color.Transparent,
                                start = glide,
                                end = Offset(size.width, size.height) + glide,
                            ),
                        )
                    },
            )
        }
    }
}

/**
 * The colours of the centred poster, blurred into a soft glow behind the carousel.
 * Neighbouring posters cross-fade in as you swipe.
 */
@Composable
private fun AmbientGlow(
    series: List<Series>,
    pagerState: PagerState,
    tilt: TiltState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    // Clipped so the enlarged, drifting glow never spills over the top bar.
    Box(modifier = modifier.clipToBounds()) {
        // The deepest layer: drifts furthest against the tilt. Slightly enlarged so its edges never show.
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    translationX = -tilt.x * TiltGlowShift.toPx()
                    translationY = -tilt.y * TiltGlowShift.toPx()
                    scaleX = 1.15f
                    scaleY = 1.15f
                },
        ) {
            series.forEachIndexed { index, item ->
                // Only the centred card and its neighbours can be visible; skip the rest.
                if ((index - pagerState.currentPage).absoluteValue > 1) return@forEachIndexed
                AsyncImage(
                    // A tiny 12×18 image stretched to fill the area looks blurry on every Android version,
                    // and costs almost nothing to load.
                    model = remember(item.posterUrl) {
                        ImageRequest.Builder(context).data(item.posterUrl).size(12, 18).build()
                    },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = (1f - pagerState.signedOffsetFor(index).absoluteValue).coerceIn(0f, 1f) * 0.7f
                        }
                        // Extra smoothing on Android 12+; ignored on older versions.
                        .blur(32.dp, BlurredEdgeTreatment.Rectangle),
                )
            }
        }
        // Fades the glow into the page at the top and bottom edges.
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0f to VidyaColors.Background,
                        0.25f to VidyaColors.Background.copy(alpha = 0.35f),
                        0.7f to VidyaColors.Background.copy(alpha = 0.5f),
                        1f to VidyaColors.Background,
                    ),
                ),
        )
    }
}

/** How far [page] is from the centre: negative to the left, positive to the right. */
private fun PagerState.signedOffsetFor(page: Int): Float =
    (page - currentPage) - currentPageOffsetFraction

@Composable
private fun PagerDots(count: Int, current: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth(),
    ) {
        repeat(count) { index ->
            val selected = index == current
            Box(
                modifier = Modifier
                    .animateContentSize()
                    .height(6.dp)
                    .width(if (selected) 18.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (selected) VidyaColors.Gold else VidyaColors.TextTertiary),
            )
        }
    }
}

@Composable
private fun ContinueWatchingRow(items: List<ContinueWatching>, onSeriesClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(top = VidyaSpacing.xl)) {
        SectionTitle(stringResource(R.string.continue_watching))
        LazyRow(
            contentPadding = PaddingValues(horizontal = VidyaSpacing.l),
            horizontalArrangement = Arrangement.spacedBy(VidyaSpacing.m),
        ) {
            items(items, key = { it.series.id }) { item ->
                ContinueWatchingCard(item = item, onClick = { onSeriesClick(item.series.id) })
            }
        }
    }
}

@Composable
private fun ContinueWatchingCard(item: ContinueWatching, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick),
    ) {
        PosterCard(
            posterUrl = item.series.posterUrl,
            contentDescription = item.series.title,
            viewCount = item.series.viewCount,
            onClick = onClick,
            progress = item.progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(156.dp),
        )
        Spacer(Modifier.height(VidyaSpacing.s))
        Text(
            text = item.series.title,
            style = MaterialTheme.typography.bodyMedium,
            color = VidyaColors.TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(R.string.episode_progress, item.lastEpisodeNumber, item.series.totalEpisodes),
            style = MaterialTheme.typography.bodySmall,
            color = VidyaColors.TextSecondary,
        )
    }
}

@Composable
private fun HomeSkeleton() {
    Column(modifier = Modifier.padding(top = VidyaSpacing.s)) {
        SkeletonBox(
            modifier = Modifier
                .padding(horizontal = 56.dp)
                .fillMaxWidth()
                .height(320.dp),
        )
        Spacer(Modifier.height(VidyaSpacing.xl + VidyaSpacing.l))
        SkeletonBox(
            modifier = Modifier
                .padding(horizontal = VidyaSpacing.l)
                .size(width = 160.dp, height = 20.dp),
        )
        Spacer(Modifier.height(VidyaSpacing.m))
        Row(
            horizontalArrangement = Arrangement.spacedBy(VidyaSpacing.m),
            modifier = Modifier.padding(horizontal = VidyaSpacing.l),
        ) {
            repeat(3) { SkeletonBox(modifier = Modifier.size(width = 120.dp, height = 156.dp)) }
        }
    }
}

// region Previews

private val previewSeries = List(5) { index ->
    Series(
        id = "s$index",
        title = "Multiple Loans kaise Manage karein?",
        posterUrl = "",
        viewCount = 750_000,
        totalEpisodes = 10,
        isPremium = index % 2 == 0,
    )
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun HomeContentPreview() {
    VidyaTheme {
        HomeScreen(
            state = HomeState(
                isLoading = false,
                featured = previewSeries,
                continueWatching = previewSeries.map { ContinueWatching(it, lastEpisodeNumber = 9) },
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun HomeLoadingPreview() {
    VidyaTheme { HomeScreen(state = HomeState(isLoading = true), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun HomeErrorPreview() {
    VidyaTheme { HomeScreen(state = HomeState(isLoading = false, error = AppError.Network), onEvent = {}) }
}

// endregion
