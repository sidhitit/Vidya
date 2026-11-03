package com.cf.vidya.shared.presentation.profile

import app.cash.turbine.test
import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.data.fake.FakeLibraryRepository
import com.cf.vidya.shared.data.fake.FakeProfileRepository
import com.cf.vidya.shared.data.fake.FakeSeriesRepository
import com.cf.vidya.shared.domain.model.Series
import com.cf.vidya.shared.domain.repository.LibraryRepository
import com.cf.vidya.shared.domain.usecase.GetProfileOverviewUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(library: LibraryRepository = FakeLibraryRepository()) = ProfileViewModel(
        GetProfileOverviewUseCase(FakeProfileRepository(), library, FakeSeriesRepository()),
    )

    @Test
    fun loadsAllSectionsInParallel() = runTest {
        val vm = viewModel()
        advanceUntilIdle()

        val overview = vm.state.value.overview!!
        assertFalse(vm.state.value.isLoading)
        assertTrue(overview.profile.isPremium)
        assertTrue(overview.library.isNotEmpty())
        assertTrue(overview.continueWatching.isNotEmpty())
        // Three 800 ms requests at the same time take 800 ms, not 2400 ms.
        assertEquals(800, currentTime)
    }

    @Test
    fun anyFailureShowsError() = runTest {
        val failingLibrary = object : LibraryRepository {
            override suspend fun getSavedSeries(): AppResult<List<Series>> = AppResult.Failure(AppError.Network)
            override suspend fun setSaved(series: Series, saved: Boolean): AppResult<Unit> = error("Not used")
        }
        val vm = viewModel(failingLibrary)
        advanceUntilIdle()

        assertEquals(AppError.Network, vm.state.value.error)
        assertNull(vm.state.value.overview)
    }

    @Test
    fun copyIdSendsTheUserId() = runTest {
        val vm = viewModel()
        advanceUntilIdle()

        vm.effects.test {
            vm.onEvent(ProfileEvent.CopyIdClicked)
            assertEquals(ProfileEffect.CopyToClipboard("241378600"), awaitItem())
        }
    }
}
