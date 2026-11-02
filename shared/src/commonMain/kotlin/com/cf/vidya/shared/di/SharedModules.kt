package com.cf.vidya.shared.di

import com.cf.vidya.shared.data.fake.FakeAuthRepository
import com.cf.vidya.shared.data.fake.FakeLibraryRepository
import com.cf.vidya.shared.data.fake.FakeSeriesRepository
import com.cf.vidya.shared.domain.repository.AuthRepository
import com.cf.vidya.shared.domain.repository.LibraryRepository
import com.cf.vidya.shared.domain.repository.SeriesRepository
import com.cf.vidya.shared.domain.usecase.GetHomeFeedUseCase
import com.cf.vidya.shared.domain.usecase.GetSavedSeriesUseCase
import com.cf.vidya.shared.domain.usecase.SendOtpUseCase
import com.cf.vidya.shared.domain.usecase.VerifyOtpUseCase
import com.cf.vidya.shared.presentation.home.HomeViewModel
import com.cf.vidya.shared.presentation.library.LibraryViewModel
import com.cf.vidya.shared.presentation.login.LoginViewModel
import com.cf.vidya.shared.presentation.otp.OtpViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// Swap the Fake repositories for the real implementations in the backend phase — nothing else changes.
val dataModule = module {
    single<SeriesRepository> { FakeSeriesRepository() }
    single<AuthRepository> { FakeAuthRepository() }
    single<LibraryRepository> { FakeLibraryRepository() }
}

val domainModule = module {
    factoryOf(::GetHomeFeedUseCase)
    factoryOf(::SendOtpUseCase)
    factoryOf(::VerifyOtpUseCase)
    factoryOf(::GetSavedSeriesUseCase)
}

val presentationModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::LibraryViewModel)
    // The mobile number comes from the screen: koinViewModel { parametersOf(mobile) }
    viewModel { params -> OtpViewModel(mobile = params.get(), sendOtp = get(), verifyOtp = get()) }
}

val sharedModules = listOf(dataModule, domainModule, presentationModule)
