package com.cf.vidya.shared.data.fake

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.ExploreVideo
import com.cf.vidya.shared.domain.repository.ExploreRepository
import kotlinx.coroutines.delay

/**
 * Six vertical sample clips from the Media3 (ExoPlayer) team's public test bucket,
 * each paired with a sample series. Likes are kept in memory.
 */
class FakeExploreRepository : ExploreRepository {

    private val likedIds = mutableSetOf<String>()

    override suspend fun getFeed(): AppResult<List<ExploreVideo>> {
        delay(800)
        val videos = SampleData.series.drop(1).take(6).mapIndexed { index, series ->
            val id = "v${index + 1}"
            ExploreVideo(
                id = id,
                series = series,
                videoUrl = "https://storage.googleapis.com/exoplayer-test-media-0/shortform_${index + 1}.mp4",
                isLiked = id in likedIds,
            )
        }
        return AppResult.Success(videos)
    }

    override suspend fun setLiked(videoId: String, liked: Boolean): AppResult<Unit> {
        delay(300)
        if (liked) likedIds += videoId else likedIds -= videoId
        return AppResult.Success(Unit)
    }
}
