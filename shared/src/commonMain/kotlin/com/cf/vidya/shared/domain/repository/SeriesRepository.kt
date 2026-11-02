package com.cf.vidya.shared.domain.repository

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.HomeFeed

interface SeriesRepository {
    suspend fun getHomeFeed(): AppResult<HomeFeed>
}
