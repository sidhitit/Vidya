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
)

data class HomeFeed(
    val featured: List<Series>,
    val continueWatching: List<ContinueWatching>,
)
