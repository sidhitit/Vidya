package com.cf.vidya.shared.domain.usecase

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.ContinueWatching
import com.cf.vidya.shared.domain.model.Series
import com.cf.vidya.shared.domain.model.UserProfile
import com.cf.vidya.shared.domain.repository.LibraryRepository
import com.cf.vidya.shared.domain.repository.ProfileRepository
import com.cf.vidya.shared.domain.repository.SeriesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class ProfileOverview(
    val profile: UserProfile,
    val library: List<Series>,
    val continueWatching: List<ContinueWatching>,
)

/** Everything the Profile screen shows, loaded from three sources at the same time. */
class GetProfileOverviewUseCase(
    private val profileRepository: ProfileRepository,
    private val libraryRepository: LibraryRepository,
    private val seriesRepository: SeriesRepository,
) {
    suspend operator fun invoke(): AppResult<ProfileOverview> = coroutineScope {
        // async starts all three requests in parallel; await waits for each result.
        val profile = async { profileRepository.getProfile() }
        val library = async { libraryRepository.getSavedSeries() }
        val continueWatching = async { seriesRepository.getContinueWatching() }

        val profileResult = profile.await()
        val libraryResult = library.await()
        val continueResult = continueWatching.await()

        when {
            profileResult is AppResult.Failure -> profileResult
            libraryResult is AppResult.Failure -> libraryResult
            continueResult is AppResult.Failure -> continueResult
            else -> AppResult.Success(
                ProfileOverview(
                    profile = (profileResult as AppResult.Success).data,
                    library = (libraryResult as AppResult.Success).data,
                    continueWatching = (continueResult as AppResult.Success).data,
                ),
            )
        }
    }
}
