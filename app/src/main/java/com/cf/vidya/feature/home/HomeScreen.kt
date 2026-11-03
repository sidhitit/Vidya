package com.cf.vidya.feature.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.cf.vidya.R
import com.cf.vidya.designsystem.components.ErrorState
import com.cf.vidya.designsystem.components.PosterCard
import com.cf.vidya.designsystem.components.SectionTitle
import com.cf.vidya.designsystem.components.SkeletonBox
import com.cf.vidya.designsystem.components.VidyaTopBar
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

@Composable
private fun HeroCarousel(series: List<Series>, onSeriesClick: (String) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { series.size })
    Column {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 56.dp),
            pageSpacing = VidyaSpacing.m,
            key = { series[it].id },
        ) { page ->
            val item = series[page]
            PosterCard(
                posterUrl = item.posterUrl,
                contentDescription = item.title,
                viewCount = item.viewCount,
                onClick = { onSeriesClick(item.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .graphicsLayer {
                        // Side cards shrink slightly, like the design
                        val offset = pagerState.offsetFor(page).coerceIn(0f, 1f)
                        scaleY = lerp(1f, 0.88f, offset)
                    },
            )
        }
        Spacer(Modifier.height(VidyaSpacing.m))
        PagerDots(count = series.size, current = pagerState.currentPage)
    }
}

private fun PagerState.offsetFor(page: Int): Float =
    ((currentPage - page) + currentPageOffsetFraction).absoluteValue

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
