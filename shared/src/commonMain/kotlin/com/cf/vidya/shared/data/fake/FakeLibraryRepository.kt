package com.cf.vidya.shared.data.fake

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.Series
import com.cf.vidya.shared.domain.repository.LibraryRepository
import kotlinx.coroutines.delay

/** Keeps saved series in memory (lost when the app closes). Starts with four saved. */
class FakeLibraryRepository : LibraryRepository {

    private val saved = SampleData.series.take(4).toMutableList()

    override suspend fun getSavedSeries(): AppResult<List<Series>> {
        delay(800)
        return AppResult.Success(saved.toList())
    }

    override suspend fun setSaved(series: Series, saved: Boolean): AppResult<Unit> {
        delay(300)
        this.saved.removeAll { it.id == series.id }
        // Newest first, like most "saved" lists.
        if (saved) this.saved.add(0, series)
        return AppResult.Success(Unit)
    }
}
