package com.cf.vidya.shared.di

import com.cf.vidya.shared.data.fake.FakeAuthRepository
import com.cf.vidya.shared.data.fake.FakeExploreRepository
import com.cf.vidya.shared.data.fake.FakeLibraryRepository
import com.cf.vidya.shared.data.fake.FakeProfileRepository
import com.cf.vidya.shared.data.fake.FakeSeriesRepository
import com.cf.vidya.shared.data.fake.FakeSubscriptionRepository
import com.cf.vidya.shared.domain.repository.AuthRepository
import com.cf.vidya.shared.domain.repository.ExploreRepository
import com.cf.vidya.shared.domain.repository.LibraryRepository
import com.cf.vidya.shared.domain.repository.ProfileRepository
import com.cf.vidya.shared.domain.repository.SeriesRepository
import com.cf.vidya.shared.domain.repository.SubscriptionRepository
import com.cf.vidya.shared.domain.usecase.GetExploreFeedUseCase
import com.cf.vidya.shared.domain.usecase.GetHomeFeedUseCase
import com.cf.vidya.shared.domain.usecase.GetProfileOverviewUseCase
import com.cf.vidya.shared.domain.usecase.GetSavedSeriesUseCase
import com.cf.vidya.shared.domain.usecase.GetTrialOfferUseCase
import com.cf.vidya.shared.domain.usecase.LogoutUseCase
import com.cf.vidya.shared.domain.usecase.SendOtpUseCase
import com.cf.vidya.shared.domain.usecase.SetSeriesSavedUseCase
import com.cf.vidya.shared.domain.usecase.SetVideoLikedUseCase
import com.cf.vidya.shared.domain.usecase.StartTrialUseCase
import com.cf.vidya.shared.domain.usecase.VerifyOtpUseCase
import com.cf.vidya.shared.presentation.explore.ExploreViewModel
import com.cf.vidya.shared.presentation.home.HomeViewModel
import com.cf.vidya.shared.presentation.library.LibraryViewModel
import com.cf.vidya.shared.presentation.login.LoginViewModel
import com.cf.vidya.shared.presentation.otp.OtpViewModel
import com.cf.vidya.shared.presentation.premium.PremiumViewModel
import com.cf.vidya.shared.presentation.profile.ProfileViewModel
import com.cf.vidya.shared.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// Swap the Fake repositories for the real implementations in the backend phase — nothing else changes.
val dataModule = module {
    single<SeriesRepository> { FakeSeriesRepository() }
    single<AuthRepository> { FakeAuthRepository() }
    single<LibraryRepository> { FakeLibraryRepository() }
    single<ProfileRepository> { FakeProfileRepository() }
    single<ExploreRepository> { FakeExploreRepository() }
    single<SubscriptionRepository> { FakeSubscriptionRepository() }
}

val domainModule = module {
    factoryOf(::GetHomeFeedUseCase)
    factoryOf(::SendOtpUseCase)
    factoryOf(::VerifyOtpUseCase)
    factoryOf(::GetSavedSeriesUseCase)
    factoryOf(::GetProfileOverviewUseCase)
    factoryOf(::GetExploreFeedUseCase)
    factoryOf(::SetVideoLikedUseCase)
    factoryOf(::SetSeriesSavedUseCase)
    factoryOf(::GetTrialOfferUseCase)
    factoryOf(::StartTrialUseCase)
    factoryOf(::LogoutUseCase)
}

val presentationModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::LibraryViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ExploreViewModel)
    viewModelOf(::PremiumViewModel)
    viewModelOf(::SettingsViewModel)
    // The mobile number comes from the screen: koinViewModel { parametersOf(mobile) }
    viewModel { params -> OtpViewModel(mobile = params.get(), sendOtp = get(), verifyOtp = get()) }
}

val sharedModules = listOf(dataModule, domainModule, presentationModule)
