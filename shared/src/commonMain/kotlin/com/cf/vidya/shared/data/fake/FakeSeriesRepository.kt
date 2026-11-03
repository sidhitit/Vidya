package com.cf.vidya.shared.data.fake

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.ContinueWatching
import com.cf.vidya.shared.domain.model.HomeFeed
import com.cf.vidya.shared.domain.repository.SeriesRepository
import kotlinx.coroutines.delay

/** Sample Home feed for the UI phase, until the Supabase backend exists. */
class FakeSeriesRepository : SeriesRepository {

    override suspend fun getHomeFeed(): AppResult<HomeFeed> {
        delay(800) // so loading states are visible
        return AppResult.Success(
            HomeFeed(featured = SampleData.series.take(5), continueWatching = continueWatching()),
        )
    }

    override suspend fun getContinueWatching(): AppResult<List<ContinueWatching>> {
        delay(800)
        return AppResult.Success(continueWatching())
    }

    private fun continueWatching() = SampleData.series.drop(2).mapIndexed { index, item ->
        ContinueWatching(item, lastEpisodeNumber = (index * 3 + 2).coerceAtMost(item.totalEpisodes))
    }
}
