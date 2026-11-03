package com.cf.vidya.shared.domain.model

/** One clip in the Explore feed: a short preview of a series. */
data class ExploreVideo(
    val id: String,
    val series: Series,
    val videoUrl: String,
    val isLiked: Boolean = false,
    /** Whether the series is in My Library. */
    val isSaved: Boolean = false,
)
