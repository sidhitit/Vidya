package com.cf.vidya.shared.domain.usecase

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.ExploreVideo
import com.cf.vidya.shared.domain.model.Series
import com.cf.vidya.shared.domain.repository.ExploreRepository
import com.cf.vidya.shared.domain.repository.LibraryRepository

/** The Explore feed, with each video marked as saved if its series is in My Library. */
class GetExploreFeedUseCase(
    private val exploreRepository: ExploreRepository,
    private val libraryRepository: LibraryRepository,
) {
    suspend operator fun invoke(): AppResult<List<ExploreVideo>> {
        val feed = exploreRepository.getFeed()
        if (feed !is AppResult.Success) return feed
        // If the library can't be read, still show the feed; Save buttons just start unselected.
        val savedIds = (libraryRepository.getSavedSeries() as? AppResult.Success)
            ?.data?.map { it.id }?.toSet()
            .orEmpty()
        return AppResult.Success(feed.data.map { it.copy(isSaved = it.series.id in savedIds) })
    }
}

class SetVideoLikedUseCase(private val repository: ExploreRepository) {
    suspend operator fun invoke(videoId: String, liked: Boolean) = repository.setLiked(videoId, liked)
}

class SetSeriesSavedUseCase(private val repository: LibraryRepository) {
    suspend operator fun invoke(series: Series, saved: Boolean) = repository.setSaved(series, saved)
}
