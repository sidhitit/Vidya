package com.cf.vidya.shared.domain.usecase

import com.cf.vidya.shared.domain.repository.AuthRepository

class VerifyOtpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(mobile: String, otp: String) = repository.verifyOtp(mobile, otp)
}
