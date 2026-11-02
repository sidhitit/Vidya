package com.cf.vidya.shared.presentation.library

import app.cash.turbine.test
import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.Series
import com.cf.vidya.shared.domain.repository.LibraryRepository
import com.cf.vidya.shared.domain.usecase.GetSavedSeriesUseCase
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    private val saved = listOf(Series("s1", "Excel Mastery", "", 310_000, 15, isPremium = true))

    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(vararg results: AppResult<List<Series>>) =
        LibraryViewModel(GetSavedSeriesUseCase(QueueRepository(results.toMutableList())))

    @Test
    fun loadsSavedSeries() = runTest {
        val vm = viewModel(AppResult.Success(saved))
        advanceUntilIdle()

        assertEquals(saved, vm.state.value.series)
        assertFalse(vm.state.value.isEmpty)
    }

    @Test
    fun noSavedSeriesIsEmpty() = runTest {
        val vm = viewModel(AppResult.Success(emptyList()))
        assertFalse(vm.state.value.isEmpty) // still loading

        advanceUntilIdle()
        assertTrue(vm.state.value.isEmpty)
    }

    @Test
    fun retryAfterErrorLoadsAgain() = runTest {
        val vm = viewModel(AppResult.Failure(AppError.Network), AppResult.Success(saved))
        advanceUntilIdle()
        assertEquals(AppError.Network, vm.state.value.error)
        assertFalse(vm.state.value.isEmpty)

        vm.onEvent(LibraryEvent.RetryClicked)
        advanceUntilIdle()
        assertNull(vm.state.value.error)
        assertEquals(saved, vm.state.value.series)
    }

    @Test
    fun clicksBecomeEffects() = runTest {
        val vm = viewModel(AppResult.Success(saved))
        vm.effects.test {
            vm.onEvent(LibraryEvent.SeriesClicked("s1"))
            assertEquals(LibraryEffect.OpenSeries("s1"), awaitItem())
            vm.onEvent(LibraryEvent.ExploreClicked)
            assertEquals(LibraryEffect.OpenExplore, awaitItem())
        }
    }

    /** Returns the given results in order, one per call. */
    private class QueueRepository(private val results: MutableList<AppResult<List<Series>>>) : LibraryRepository {
        override suspend fun getSavedSeries(): AppResult<List<Series>> = results.removeAt(0)
    }
}
