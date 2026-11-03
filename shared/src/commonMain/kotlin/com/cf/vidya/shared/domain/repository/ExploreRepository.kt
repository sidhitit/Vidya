package com.cf.vidya.shared.domain.repository

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.ExploreVideo

interface ExploreRepository {
    suspend fun getFeed(): AppResult<List<ExploreVideo>>
    suspend fun setLiked(videoId: String, liked: Boolean): AppResult<Unit>
}
