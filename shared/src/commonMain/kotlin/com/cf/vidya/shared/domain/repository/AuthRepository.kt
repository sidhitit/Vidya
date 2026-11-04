package com.cf.vidya.shared.domain.repository

import com.cf.vidya.shared.core.AppResult

interface AuthRepository {
    suspend fun sendOtp(mobile: String): AppResult<Unit>
    suspend fun verifyOtp(mobile: String, otp: String): AppResult<Unit>
    suspend fun logout(): AppResult<Unit>
}
