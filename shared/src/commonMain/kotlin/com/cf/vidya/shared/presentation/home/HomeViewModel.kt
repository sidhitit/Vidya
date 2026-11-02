package com.cf.vidya.shared.presentation.home

import androidx.lifecycle.viewModelScope
import com.cf.vidya.shared.core.MviViewModel
import com.cf.vidya.shared.core.onFailure
import com.cf.vidya.shared.core.onSuccess
import com.cf.vidya.shared.domain.usecase.GetHomeFeedUseCase
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomeFeed: GetHomeFeedUseCase,
) : MviViewModel<HomeState, HomeEvent, HomeEffect>(HomeState()) {

    init {
        load()
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.RetryClicked -> load()
            HomeEvent.SearchClicked -> sendEffect(HomeEffect.OpenSearch)
            HomeEvent.ProfileClicked -> sendEffect(HomeEffect.OpenProfile)
            is HomeEvent.SeriesClicked -> sendEffect(HomeEffect.OpenSeries(event.seriesId))
        }
    }

    private fun load() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            getHomeFeed()
                .onSuccess { feed ->
                    setState {
                        copy(
                            isLoading = false,
                            featured = feed.featured,
                            continueWatching = feed.continueWatching,
                        )
                    }
                }
                .onFailure { error -> setState { copy(isLoading = false, error = error) } }
        }
    }
}
