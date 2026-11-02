package com.cf.vidya.shared.data.fake

import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.repository.AuthRepository
import kotlinx.coroutines.delay

/** Stand-in until the auth API exists. Any number gets an OTP; only [VALID_OTP] logs in. */
class FakeAuthRepository : AuthRepository {

    override suspend fun sendOtp(mobile: String): AppResult<Unit> {
        delay(800)
        return AppResult.Success(Unit)
    }

    override suspend fun verifyOtp(mobile: String, otp: String): AppResult<Unit> {
        delay(800)
        return if (otp == VALID_OTP) AppResult.Success(Unit) else AppResult.Failure(AppError.InvalidOtp)
    }

    companion object {
        const val VALID_OTP = "123456"
    }
}
