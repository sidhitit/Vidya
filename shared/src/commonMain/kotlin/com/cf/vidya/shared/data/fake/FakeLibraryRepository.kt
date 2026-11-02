package com.cf.vidya.shared.data.fake

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.Series
import com.cf.vidya.shared.domain.repository.LibraryRepository
import kotlinx.coroutines.delay

/** Pretends the user has saved six series. */
class FakeLibraryRepository : LibraryRepository {

    override suspend fun getSavedSeries(): AppResult<List<Series>> {
        delay(800)
        return AppResult.Success(SampleData.series.take(6))
    }
}
