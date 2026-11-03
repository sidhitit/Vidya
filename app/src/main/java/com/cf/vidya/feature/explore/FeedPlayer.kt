package com.cf.vidya.feature.explore

import android.view.LayoutInflater
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.cf.vidya.R

/**
 * Start playing once 0.5 s of video is buffered instead of the default 2.5 s,
 * so swiping to a new clip feels quicker. Short clips rarely need a bigger head start.
 */
private val ShortClipLoadControl = DefaultLoadControl.Builder()
    .setBufferDurationsMs(
        /* minBufferMs = */ 15_000,
        /* maxBufferMs = */ 30_000,
        /* bufferForPlaybackMs = */ 500,
        /* bufferForPlaybackAfterRebufferMs = */ 1_000,
    )
    .build()

/**
 * One ExoPlayer for the whole feed. The feed loads every clip into its playlist and jumps
 * to the clip on screen, so swiping doesn't create a new player each time.
 * Released when the Explore screen leaves the screen. Returns null in Previews, which can't play video.
 */
@Composable
fun rememberFeedPlayer(): ExoPlayer? {
    if (LocalInspectionMode.current) return null
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context)
            .setLoadControl(ShortClipLoadControl)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE // loop the current clip, like Reels
            }
    }
    DisposableEffect(player) {
        onDispose { player.release() }
    }
    return player
}

/**
 * Shows the player's video, cropped to fill the screen. Has no controls of its own.
 * Settings (TextureView, crop, no controls) are in res/layout/feed_player_view.xml.
 */
@Composable
fun VideoSurface(player: Player, modifier: Modifier = Modifier) {
    // PlayerView is a classic Android View; AndroidView puts it inside Compose.
    AndroidView(
        factory = { context ->
            LayoutInflater.from(context).inflate(R.layout.feed_player_view, null) as PlayerView
        },
        update = { it.player = player },
        onRelease = { it.player = null },
        modifier = modifier,
    )
}
