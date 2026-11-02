package com.cf.vidya.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cf.vidya.R
import com.cf.vidya.designsystem.components.EmptyState
import com.cf.vidya.designsystem.components.ErrorState
import com.cf.vidya.designsystem.components.PosterCard
import com.cf.vidya.designsystem.components.SkeletonBox
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing
import com.cf.vidya.designsystem.theme.VidyaTheme
import com.cf.vidya.feature.common.messageRes
import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.domain.model.Series
import com.cf.vidya.shared.presentation.library.LibraryEvent
import com.cf.vidya.shared.presentation.library.LibraryState

/** Posters in the design are 3:4 (width:height). */
private const val PosterAspectRatio = 3f / 4f

/** 2 columns on phones in portrait, more on landscape and tablets. */
private val GridColumns = GridCells.Adaptive(minSize = 130.dp)

@Composable
fun LibraryScreen(
    state: LibraryState,
    onEvent: (LibraryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VidyaColors.Background)
            .statusBarsPadding(),
    ) {
        Text(
            text = stringResource(R.string.my_library),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = VidyaColors.TextPrimary,
            modifier = Modifier.padding(horizontal = VidyaSpacing.l, vertical = VidyaSpacing.m),
        )

        val error = state.error
        when {
            state.isLoading -> LibrarySkeleton()
            error != null -> ErrorState(
                message = stringResource(error.messageRes()),
                onRetry = { onEvent(LibraryEvent.RetryClicked) },
            )
            state.isEmpty -> EmptyState(
                icon = R.drawable.ic_bookmark_outlined,
                title = stringResource(R.string.library_empty_title),
                message = stringResource(R.string.library_empty_message),
                actionText = stringResource(R.string.library_empty_action),
                onAction = { onEvent(LibraryEvent.ExploreClicked) },
                // Keeps the button above the floating bottom bar.
                modifier = Modifier.padding(bottom = VidyaSpacing.bottomBarClearance),
            )
            else -> SeriesGrid(
                series = state.series,
                onSeriesClick = { onEvent(LibraryEvent.SeriesClicked(it)) },
            )
        }
    }
}

@Composable
private fun SeriesGrid(series: List<Series>, onSeriesClick: (String) -> Unit) {
    // Like LazyColumn, but arranges items in columns and only builds the visible ones.
    LazyVerticalGrid(
        columns = GridColumns,
        horizontalArrangement = Arrangement.spacedBy(VidyaSpacing.m),
        verticalArrangement = Arrangement.spacedBy(VidyaSpacing.m),
        contentPadding = PaddingValues(
            start = VidyaSpacing.l,
            end = VidyaSpacing.l,
            top = VidyaSpacing.xs,
            bottom = VidyaSpacing.bottomBarClearance,
        ),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(series, key = { it.id }) { item ->
            PosterCard(
                posterUrl = item.posterUrl,
                contentDescription = item.title,
                viewCount = item.viewCount,
                onClick = { onSeriesClick(item.id) },
                modifier = Modifier.aspectRatio(PosterAspectRatio),
            )
        }
    }
}

@Composable
private fun LibrarySkeleton() {
    LazyVerticalGrid(
        columns = GridColumns,
        horizontalArrangement = Arrangement.spacedBy(VidyaSpacing.m),
        verticalArrangement = Arrangement.spacedBy(VidyaSpacing.m),
        contentPadding = PaddingValues(horizontal = VidyaSpacing.l, vertical = VidyaSpacing.xs),
        userScrollEnabled = false,
        modifier = Modifier.fillMaxSize(),
    ) {
        items(6) { SkeletonBox(modifier = Modifier.aspectRatio(PosterAspectRatio)) }
    }
}

// region Previews

private val previewSeries = List(6) { index ->
    Series(
        id = "s$index",
        title = "Redemption",
        posterUrl = "",
        viewCount = 750_000,
        totalEpisodes = 10,
        isPremium = false,
    )
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun LibraryContentPreview() {
    VidyaTheme { LibraryScreen(state = LibraryState(isLoading = false, series = previewSeries), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun LibraryEmptyPreview() {
    VidyaTheme { LibraryScreen(state = LibraryState(isLoading = false), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun LibraryLoadingPreview() {
    VidyaTheme { LibraryScreen(state = LibraryState(), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun LibraryErrorPreview() {
    VidyaTheme { LibraryScreen(state = LibraryState(isLoading = false, error = AppError.Network), onEvent = {}) }
}

// endregion
