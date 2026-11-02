package com.cf.vidya.shared.domain.repository

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.Series

/** The series the user has saved to My Library. */
interface LibraryRepository {
    suspend fun getSavedSeries(): AppResult<List<Series>>
}
