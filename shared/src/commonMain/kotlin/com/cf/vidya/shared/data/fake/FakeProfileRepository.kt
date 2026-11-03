package com.cf.vidya.shared.data.fake

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.UserProfile
import com.cf.vidya.shared.domain.repository.ProfileRepository
import kotlinx.coroutines.delay

/** A premium user, as in the design. */
class FakeProfileRepository : ProfileRepository {

    override suspend fun getProfile(): AppResult<UserProfile> {
        delay(800)
        return AppResult.Success(UserProfile(id = "241378600", isPremium = true))
    }
}
