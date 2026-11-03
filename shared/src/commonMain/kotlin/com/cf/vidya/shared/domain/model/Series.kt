package com.cf.vidya.shared.domain.model

data class Series(
    val id: String,
    val title: String,
    val posterUrl: String,
    val viewCount: Long,
    val totalEpisodes: Int,
    val isPremium: Boolean,
)

/** A series the user has started, with where they left off. */
data class ContinueWatching(
    val series: Series,
    val lastEpisodeNumber: Int,
) {
    /**
     * How far through the series the user is, from 0 to 1 (Ep 9 of 10 → 0.9).
     * Counted in episodes for now; the backend may later send exact watch time.
     */
    val progress: Float
        get() = if (series.totalEpisodes <= 0) 0f
        else (lastEpisodeNumber.toFloat() / series.totalEpisodes).coerceIn(0f, 1f)
}

data class HomeFeed(
    val featured: List<Series>,
    val continueWatching: List<ContinueWatching>,
)
