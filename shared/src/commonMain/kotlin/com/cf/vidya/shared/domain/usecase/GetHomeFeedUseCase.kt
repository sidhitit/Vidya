package com.cf.vidya.shared.domain.usecase

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.HomeFeed
import com.cf.vidya.shared.domain.repository.SeriesRepository

class GetHomeFeedUseCase(private val repository: SeriesRepository) {
    suspend operator fun invoke(): AppResult<HomeFeed> = repository.getHomeFeed()
}
