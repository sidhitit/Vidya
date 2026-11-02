package com.cf.vidya.shared.domain.usecase

import com.cf.vidya.shared.domain.repository.LibraryRepository

class GetSavedSeriesUseCase(private val repository: LibraryRepository) {
    suspend operator fun invoke() = repository.getSavedSeries()
}
