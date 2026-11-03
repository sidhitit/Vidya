package com.cf.vidya.shared.presentation.explore

import app.cash.turbine.test
import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.data.fake.FakeExploreRepository
import com.cf.vidya.shared.data.fake.FakeLibraryRepository
import com.cf.vidya.shared.domain.model.ExploreVideo
import com.cf.vidya.shared.domain.repository.ExploreRepository
import com.cf.vidya.shared.domain.usecase.GetExploreFeedUseCase
import com.cf.vidya.shared.domain.usecase.SetSeriesSavedUseCase
import com.cf.vidya.shared.domain.usecase.SetVideoLikedUseCase
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(
        explore: ExploreRepository = FakeExploreRepository(),
        library: FakeLibraryRepository = FakeLibraryRepository(),
    ) = ExploreViewModel(
        GetExploreFeedUseCase(explore, library),
        SetVideoLikedUseCase(explore),
        SetSeriesSavedUseCase(library),
    )

    private fun ExploreViewModel.video(id: String) = state.value.videos.first { it.id == id }

    @Test
    fun feedMarksSeriesAlreadyInLibrary() = runTest {
        val vm = viewModel()
        advanceUntilIdle()

        // The fake library starts with s1–s4; the feed covers s2–s7.
        assertEquals(listOf(true, true, true, false, false, false), vm.state.value.videos.map { it.isSaved })
    }

    @Test
    fun likeFlipsStraightAway() = runTest {
        val vm = viewModel()
        advanceUntilIdle()

        vm.onEvent(ExploreEvent.LikeClicked("v1"))
        assertTrue(vm.video("v1").isLiked) // before the request finishes
    }

    @Test
    fun likeIsUndoneIfTheRequestFails() = runTest {
        val failingLikes = object : ExploreRepository by FakeExploreRepository() {
            override suspend fun setLiked(videoId: String, liked: Boolean) = AppResult.Failure(AppError.Network)
        }
        val vm = viewModel(explore = failingLikes)
        advanceUntilIdle()

        vm.onEvent(ExploreEvent.LikeClicked("v1"))
        assertTrue(vm.video("v1").isLiked)
        advanceUntilIdle()
        assertFalse(vm.video("v1").isLiked)
    }

    @Test
    fun saveAddsTheSeriesToLibrary() = runTest {
        val library = FakeLibraryRepository()
        val vm = viewModel(library = library)
        advanceUntilIdle()
        val unsaved = vm.state.value.videos.first { !it.isSaved }

        vm.onEvent(ExploreEvent.SaveClicked(unsaved.id))
        advanceUntilIdle()

        assertTrue(vm.video(unsaved.id).isSaved)
        val savedIds = (library.getSavedSeries() as AppResult.Success).data.map { it.id }
        assertTrue(unsaved.series.id in savedIds)
    }

    @Test
    fun watchNowAndShareBecomeEffects() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        val first: ExploreVideo = vm.state.value.videos.first()

        vm.effects.test {
            vm.onEvent(ExploreEvent.WatchNowClicked(first.id))
            assertEquals(ExploreEffect.OpenSeries(first.series.id), awaitItem())
            vm.onEvent(ExploreEvent.ShareClicked(first.id))
            assertEquals(ExploreEffect.ShareSeries(first.series.title), awaitItem())
        }
    }
}
