package com.cf.vidya.feature.explore

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import coil3.compose.AsyncImage
import com.cf.vidya.R
import com.cf.vidya.designsystem.components.ErrorState
import com.cf.vidya.designsystem.components.GoldButton
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing
import com.cf.vidya.designsystem.theme.VidyaTheme
import com.cf.vidya.feature.common.messageRes
import com.cf.vidya.shared.domain.model.ExploreVideo
import com.cf.vidya.shared.domain.model.Series
import com.cf.vidya.shared.presentation.explore.ExploreEvent
import com.cf.vidya.shared.presentation.explore.ExploreState

/** Room left at the bottom for the floating bottom bar. */
private val BottomBarSpace = 88.dp

@Composable
fun ExploreScreen(
    state: ExploreState,
    onEvent: (ExploreEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        val error = state.error
        when {
            state.isLoading -> CircularProgressIndicator(
                color = VidyaColors.Gold,
                modifier = Modifier.align(Alignment.Center),
            )
            error != null -> ErrorState(
                message = stringResource(error.messageRes()),
                onRetry = { onEvent(ExploreEvent.RetryClicked) },
            )
            else -> ExploreFeed(videos = state.videos, onEvent = onEvent)
        }
    }
}

@Composable
private fun ExploreFeed(videos: List<ExploreVideo>, onEvent: (ExploreEvent) -> Unit) {
    val pagerState = rememberPagerState { videos.size }
    val player = rememberFeedPlayer()
    // True when the user tapped to pause; stops us auto-resuming over their choice.
    var pausedByUser by remember { mutableStateOf(false) }
    // Which clip has drawn its first frame; until then its page keeps showing the poster.
    var firstFrameIndex by remember { mutableStateOf<Int?>(null) }
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onRenderedFirstFrame() {
                firstFrameIndex = player?.currentMediaItemIndex
            }
        }
        player?.addListener(listener)
        onDispose { player?.removeListener(listener) }
    }

    // Only the URLs matter to the player; liking or saving must not restart the video.
    val urls = remember(videos) { videos.map { it.videoUrl } }
    LaunchedEffect(player, urls) {
        if (player == null) return@LaunchedEffect
        player.setMediaItems(urls.map(MediaItem::fromUri), pagerState.settledPage, 0L)
        player.prepare()
        // settledPage changes once a swipe has finished: play that clip from the start.
        snapshotFlow { pagerState.settledPage }.collect { page ->
            if (player.currentMediaItemIndex != page) player.seekTo(page, 0L)
            pausedByUser = false
            player.play()
        }
    }
    // Pause when the app goes to the background, and carry on when it comes back.
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { player?.pause() }
    LifecycleEventEffect(Lifecycle.Event.ON_START) { if (!pausedByUser) player?.play() }

    VerticalPager(
        state = pagerState,
        key = { videos[it].id },
        modifier = Modifier.fillMaxSize(),
    ) { page ->
        val isCurrent = page == pagerState.settledPage
        ExplorePage(
            video = videos[page],
            player = if (isCurrent) player else null,
            showPoster = !(isCurrent && firstFrameIndex == page),
            showPlayIcon = isCurrent && pausedByUser,
            onTap = {
                if (player == null) return@ExplorePage
                pausedByUser = !pausedByUser
                if (pausedByUser) player.pause() else player.play()
            },
            onEvent = onEvent,
        )
    }
}

@Composable
private fun ExplorePage(
    video: ExploreVideo,
    player: Player?,
    showPoster: Boolean,
    showPlayIcon: Boolean,
    onTap: () -> Unit,
    onEvent: (ExploreEvent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { onTap() }) },
    ) {
        if (player != null) {
            VideoSurface(player = player, modifier = Modifier.fillMaxSize())
        }
        // Drawn over the video until its first frame, and on the pages next to the current one,
        // so you see the poster instead of a black screen while a clip loads.
        if (showPoster) {
            AsyncImage(
                model = video.series.posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        if (showPlayIcon) {
            Icon(
                painter = painterResource(R.drawable.ic_play),
                contentDescription = stringResource(R.string.play),
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(72.dp),
            )
        }
        // Darkens the bottom so the white text stays readable on any video.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)))),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = VidyaSpacing.l, end = VidyaSpacing.l, bottom = BottomBarSpace),
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                SeriesInfo(series = video.series, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(VidyaSpacing.m))
                VideoActions(video = video, onEvent = onEvent)
            }
            Spacer(Modifier.height(VidyaSpacing.m))
            GoldButton(
                text = stringResource(R.string.watch_now),
                onClick = { onEvent(ExploreEvent.WatchNowClicked(video.id)) },
            )
        }
    }
}

@Composable
private fun SeriesInfo(series: Series, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        AsyncImage(
            model = series.posterUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 44.dp, height = 60.dp)
                .clip(MaterialTheme.shapes.small)
                .background(VidyaColors.Surface),
        )
        Spacer(Modifier.width(VidyaSpacing.m))
        Column {
            Text(
                text = series.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = VidyaColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = pluralStringResource(R.plurals.episode_count, series.totalEpisodes, series.totalEpisodes),
                style = MaterialTheme.typography.bodySmall,
                color = VidyaColors.TextSecondary,
            )
        }
    }
}

@Composable
private fun VideoActions(video: ExploreVideo, onEvent: (ExploreEvent) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VidyaSpacing.l),
    ) {
        ActionButton(
            icon = if (video.isLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart_outlined,
            label = stringResource(R.string.like),
            selected = video.isLiked,
            onClick = { onEvent(ExploreEvent.LikeClicked(video.id)) },
        )
        ActionButton(
            icon = if (video.isSaved) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outlined,
            label = stringResource(R.string.save),
            selected = video.isSaved,
            onClick = { onEvent(ExploreEvent.SaveClicked(video.id)) },
        )
        ActionButton(
            icon = R.drawable.ic_share,
            label = stringResource(R.string.share),
            selected = null,
            onClick = { onEvent(ExploreEvent.ShareClicked(video.id)) },
        )
    }
}

/** Icon with a small label under it. [selected] is null for buttons that aren't on/off (Share). */
@Composable
private fun ActionButton(
    @DrawableRes icon: Int,
    label: String,
    selected: Boolean?,
    onClick: () -> Unit,
) {
    val tint = if (selected == true) VidyaColors.Gold else Color.White
    val clickModifier = if (selected != null) {
        // Tells TalkBack this is an on/off switch and whether it's on.
        Modifier.toggleable(value = selected, role = Role.Switch, onValueChange = { onClick() })
    } else {
        Modifier.pointerInput(Unit) { detectTapGestures(onTap = { onClick() }) }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .then(clickModifier)
            .padding(VidyaSpacing.xs),
    ) {
        Icon(painter = painterResource(icon), contentDescription = null, tint = tint, modifier = Modifier.size(28.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = tint)
    }
}

// region Previews

private val previewVideo = ExploreVideo(
    id = "v1",
    series = Series("s1", "Redemption : New Show", "", 750_000, 25, isPremium = false),
    videoUrl = "",
    isSaved = true,
)

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun ExploreFeedPreview() {
    VidyaTheme { ExploreScreen(state = ExploreState(isLoading = false, videos = listOf(previewVideo)), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun ExploreLoadingPreview() {
    VidyaTheme { ExploreScreen(state = ExploreState(), onEvent = {}) }
}

// endregion
