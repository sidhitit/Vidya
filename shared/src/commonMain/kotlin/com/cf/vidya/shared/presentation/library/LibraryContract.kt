package com.cf.vidya.shared.presentation.library

import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.domain.model.Series

data class LibraryState(
    val isLoading: Boolean = true,
    val error: AppError? = null,
    val series: List<Series> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && error == null && series.isEmpty()
}

sealed interface LibraryEvent {
    data object RetryClicked : LibraryEvent
    data object ExploreClicked : LibraryEvent
    data class SeriesClicked(val seriesId: String) : LibraryEvent
}

sealed interface LibraryEffect {
    data object OpenExplore : LibraryEffect
    data class OpenSeries(val seriesId: String) : LibraryEffect
}
