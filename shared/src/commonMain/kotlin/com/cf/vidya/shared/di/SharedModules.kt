package com.cf.vidya.shared.di

import com.cf.vidya.shared.data.fake.FakeSeriesRepository
import com.cf.vidya.shared.domain.repository.SeriesRepository
import com.cf.vidya.shared.domain.usecase.GetHomeFeedUseCase
import com.cf.vidya.shared.presentation.home.HomeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// Swap FakeSeriesRepository for the real implementation in the backend phase — nothing else changes.
val dataModule = module {
    single<SeriesRepository> { FakeSeriesRepository() }
}

val domainModule = module {
    factoryOf(::GetHomeFeedUseCase)
}

val presentationModule = module {
    viewModelOf(::HomeViewModel)
}

val sharedModules = listOf(dataModule, domainModule, presentationModule)
