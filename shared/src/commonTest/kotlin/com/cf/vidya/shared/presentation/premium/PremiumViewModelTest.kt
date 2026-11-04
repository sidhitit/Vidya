package com.cf.vidya.shared.presentation.premium

import app.cash.turbine.test
import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.data.fake.FakeSubscriptionRepository
import com.cf.vidya.shared.domain.repository.SubscriptionRepository
import com.cf.vidya.shared.domain.usecase.GetTrialOfferUseCase
import com.cf.vidya.shared.domain.usecase.StartTrialUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PremiumViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(repository: SubscriptionRepository = FakeSubscriptionRepository()) =
        PremiumViewModel(GetTrialOfferUseCase(repository), StartTrialUseCase(repository))

    @Test
    fun loadsTheOffer() = runTest {
        val vm = viewModel()
        assertTrue(vm.state.value.isLoading)
        advanceUntilIdle()

        assertFalse(vm.state.value.isLoading)
        assertEquals(1, assertNotNull(vm.state.value.offer).trialPrice)
    }

    @Test
    fun startingTheTrialShowsProgressThenReportsSuccess() = runTest {
        val vm = viewModel()
        advanceUntilIdle()

        vm.effects.test {
            vm.onEvent(PremiumEvent.StartTrialClicked)
            assertTrue(vm.state.value.isStartingTrial)
            assertEquals(PremiumEffect.TrialStarted, awaitItem())
            advanceUntilIdle()
            assertFalse(vm.state.value.isStartingTrial)
        }
    }

    @Test
    fun repeatTapsStartTheTrialOnce() = runTest {
        var calls = 0
        val counting = object : SubscriptionRepository by FakeSubscriptionRepository() {
            override suspend fun startTrial(offerId: String): AppResult<Unit> {
                calls++
                return AppResult.Success(Unit)
            }
        }
        val vm = viewModel(counting)
        advanceUntilIdle()

        repeat(3) { vm.onEvent(PremiumEvent.StartTrialClicked) }
        advanceUntilIdle()
        assertEquals(1, calls)
    }

    @Test
    fun failedTrialShowsTheError() = runTest {
        val failing = object : SubscriptionRepository by FakeSubscriptionRepository() {
            override suspend fun startTrial(offerId: String) = AppResult.Failure(AppError.Network)
        }
        val vm = viewModel(failing)
        advanceUntilIdle()

        vm.effects.test {
            vm.onEvent(PremiumEvent.StartTrialClicked)
            assertEquals(PremiumEffect.ShowError(AppError.Network), awaitItem())
        }
    }
}
