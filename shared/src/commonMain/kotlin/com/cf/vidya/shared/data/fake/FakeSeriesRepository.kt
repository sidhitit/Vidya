package com.cf.vidya.shared.data.fake

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.ContinueWatching
import com.cf.vidya.shared.domain.model.HomeFeed
import com.cf.vidya.shared.domain.model.Series
import com.cf.vidya.shared.domain.repository.SeriesRepository
import kotlinx.coroutines.delay

/**
 * Sample data for the UI phase, until the Supabase backend exists.
 * Posters are random placeholder images, not real artwork.
 */
class FakeSeriesRepository : SeriesRepository {

    override suspend fun getHomeFeed(): AppResult<HomeFeed> {
        delay(800) // so loading states are visible
        return AppResult.Success(
            HomeFeed(
                featured = sampleSeries.take(5),
                continueWatching = sampleSeries.drop(2).mapIndexed { index, series ->
                    ContinueWatching(series, lastEpisodeNumber = (index * 3 + 2).coerceAtMost(series.totalEpisodes))
                },
            ),
        )
    }

    private companion object {
        fun poster(seed: String) = "https://picsum.photos/seed/vidya-$seed/400/600"

        val sampleSeries = listOf(
            Series("s1", "Multiple Loans kaise Manage karein?", poster("loans"), 750_000, 10, isPremium = false),
            Series("s2", "English Speaking in 30 Days", poster("english"), 1_200_000, 30, isPremium = true),
            Series("s3", "Stock Market Basics", poster("stocks"), 540_000, 12, isPremium = true),
            Series("s4", "Interview Crack Karo", poster("interview"), 980_000, 8, isPremium = false),
            Series("s5", "Excel Mastery", poster("excel"), 310_000, 15, isPremium = true),
            Series("s6", "Personal Finance 101", poster("finance"), 670_000, 10, isPremium = true),
            Series("s7", "Freelancing se Kamai", poster("freelance"), 450_000, 9, isPremium = false),
        )
    }
}
