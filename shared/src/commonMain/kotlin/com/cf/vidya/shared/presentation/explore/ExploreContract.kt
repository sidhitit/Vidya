package com.cf.vidya.shared.presentation.explore

import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.domain.model.ExploreVideo

data class ExploreState(
    val isLoading: Boolean = true,
    val error: AppError? = null,
    val videos: List<ExploreVideo> = emptyList(),
)

sealed interface ExploreEvent {
    data object RetryClicked : ExploreEvent
    data class LikeClicked(val videoId: String) : ExploreEvent
    data class SaveClicked(val videoId: String) : ExploreEvent
    data class ShareClicked(val videoId: String) : ExploreEvent
    data class WatchNowClicked(val videoId: String) : ExploreEvent
}

sealed interface ExploreEffect {
    data class OpenSeries(val seriesId: String) : ExploreEffect
    data class ShareSeries(val seriesTitle: String) : ExploreEffect
}
