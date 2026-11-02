package com.cf.vidya.shared.presentation.home

import app.cash.turbine.test
import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.ContinueWatching
import com.cf.vidya.shared.domain.model.HomeFeed
import com.cf.vidya.shared.domain.model.Series
import com.cf.vidya.shared.domain.repository.SeriesRepository
import com.cf.vidya.shared.domain.usecase.GetHomeFeedUseCase
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
class HomeViewModelTest {

    private val series = Series("s1", "Excel Mastery", "", 310_000, 15, isPremium = true)
    private val feed = HomeFeed(
        featured = listOf(series),
        continueWatching = listOf(ContinueWatching(series, lastEpisodeNumber = 3)),
    )

    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(vararg results: AppResult<HomeFeed>) =
        HomeViewModel(GetHomeFeedUseCase(QueueRepository(results.toMutableList())))

    @Test
    fun loadsFeedOnStart() = runTest {
        val vm = viewModel(AppResult.Success(feed))
        assertTrue(vm.state.value.isLoading)

        advanceUntilIdle()

        val state = vm.state.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(feed.featured, state.featured)
        assertEquals(feed.continueWatching, state.continueWatching)
    }

    @Test
    fun showsErrorThenRecoversOnRetry() = runTest {
        val vm = viewModel(AppResult.Failure(AppError.Network), AppResult.Success(feed))
        advanceUntilIdle()
        assertEquals(AppError.Network, vm.state.value.error)

        vm.onEvent(HomeEvent.RetryClicked)
        advanceUntilIdle()

        assertNull(vm.state.value.error)
        assertEquals(feed.featured, vm.state.value.featured)
    }

    @Test
    fun seriesClickOpensSeries() = runTest {
        val vm = viewModel(AppResult.Success(feed))

        vm.effects.test {
            vm.onEvent(HomeEvent.SeriesClicked("s1"))
            assertEquals(HomeEffect.OpenSeries("s1"), awaitItem())
        }
    }

    /** Returns the given results in order, one per call. */
    private class QueueRepository(private val results: MutableList<AppResult<HomeFeed>>) : SeriesRepository {
        override suspend fun getHomeFeed(): AppResult<HomeFeed> = results.removeAt(0)
    }
}
