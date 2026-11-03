package com.cf.vidya.shared.domain.repository

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getProfile(): AppResult<UserProfile>
}
