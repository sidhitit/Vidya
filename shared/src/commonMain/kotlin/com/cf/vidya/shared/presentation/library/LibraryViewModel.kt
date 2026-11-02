package com.cf.vidya.shared.presentation.library

import androidx.lifecycle.viewModelScope
import com.cf.vidya.shared.core.MviViewModel
import com.cf.vidya.shared.core.onFailure
import com.cf.vidya.shared.core.onSuccess
import com.cf.vidya.shared.domain.usecase.GetSavedSeriesUseCase
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val getSavedSeries: GetSavedSeriesUseCase,
) : MviViewModel<LibraryState, LibraryEvent, LibraryEffect>(LibraryState()) {

    init {
        load()
    }

    override fun onEvent(event: LibraryEvent) {
        when (event) {
            LibraryEvent.RetryClicked -> load()
            LibraryEvent.ExploreClicked -> sendEffect(LibraryEffect.OpenExplore)
            is LibraryEvent.SeriesClicked -> sendEffect(LibraryEffect.OpenSeries(event.seriesId))
        }
    }

    private fun load() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            getSavedSeries()
                .onSuccess { setState { copy(isLoading = false, series = it) } }
                .onFailure { setState { copy(isLoading = false, error = it) } }
        }
    }
}
