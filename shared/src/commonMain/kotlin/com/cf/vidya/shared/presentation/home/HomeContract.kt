package com.cf.vidya.shared.presentation.home

import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.domain.model.ContinueWatching
import com.cf.vidya.shared.domain.model.Series

data class HomeState(
    val isLoading: Boolean = true,
    val error: AppError? = null,
    val featured: List<Series> = emptyList(),
    val continueWatching: List<ContinueWatching> = emptyList(),
)

sealed interface HomeEvent {
    data object RetryClicked : HomeEvent
    data object SearchClicked : HomeEvent
    data object ProfileClicked : HomeEvent
    data class SeriesClicked(val seriesId: String) : HomeEvent
}

sealed interface HomeEffect {
    data object OpenSearch : HomeEffect
    data object OpenProfile : HomeEffect
    data class OpenSeries(val seriesId: String) : HomeEffect
}
