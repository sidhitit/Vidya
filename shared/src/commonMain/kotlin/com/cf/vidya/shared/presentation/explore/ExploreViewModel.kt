package com.cf.vidya.shared.presentation.explore

import androidx.lifecycle.viewModelScope
import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.core.MviViewModel
import com.cf.vidya.shared.core.onFailure
import com.cf.vidya.shared.core.onSuccess
import com.cf.vidya.shared.domain.model.ExploreVideo
import com.cf.vidya.shared.domain.usecase.GetExploreFeedUseCase
import com.cf.vidya.shared.domain.usecase.SetSeriesSavedUseCase
import com.cf.vidya.shared.domain.usecase.SetVideoLikedUseCase
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val getFeed: GetExploreFeedUseCase,
    private val setLiked: SetVideoLikedUseCase,
    private val setSaved: SetSeriesSavedUseCase,
) : MviViewModel<ExploreState, ExploreEvent, ExploreEffect>(ExploreState()) {

    init {
        load()
    }

    override fun onEvent(event: ExploreEvent) {
        when (event) {
            ExploreEvent.RetryClicked -> load()
            is ExploreEvent.LikeClicked -> toggle(
                videoId = event.videoId,
                isOn = { it.isLiked },
                withValue = { video, on -> video.copy(isLiked = on) },
                request = { video, on -> setLiked(video.id, on) },
            )
            is ExploreEvent.SaveClicked -> toggle(
                videoId = event.videoId,
                isOn = { it.isSaved },
                withValue = { video, on -> video.copy(isSaved = on) },
                request = { video, on -> setSaved(video.series, on) },
            )
            is ExploreEvent.ShareClicked -> video(event.videoId)?.let {
                sendEffect(ExploreEffect.ShareSeries(it.series.title))
            }
            is ExploreEvent.WatchNowClicked -> video(event.videoId)?.let {
                sendEffect(ExploreEffect.OpenSeries(it.series.id))
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            getFeed()
                .onSuccess { setState { copy(isLoading = false, videos = it) } }
                .onFailure { setState { copy(isLoading = false, error = it) } }
        }
    }

    /**
     * Optimistic toggle: flip the button straight away so it feels instant,
     * then tell the server, and flip it back if that fails.
     */
    private fun toggle(
        videoId: String,
        isOn: (ExploreVideo) -> Boolean,
        withValue: (ExploreVideo, Boolean) -> ExploreVideo,
        request: suspend (ExploreVideo, Boolean) -> AppResult<Unit>,
    ) {
        val video = video(videoId) ?: return
        val newValue = !isOn(video)
        update(videoId) { withValue(it, newValue) }
        viewModelScope.launch {
            request(video, newValue).onFailure { update(videoId) { withValue(it, !newValue) } }
        }
    }

    private fun video(id: String) = currentState.videos.firstOrNull { it.id == id }

    private fun update(id: String, change: (ExploreVideo) -> ExploreVideo) = setState {
        copy(videos = videos.map { if (it.id == id) change(it) else it })
    }
}
