package com.cf.vidya.shared.domain.usecase

import com.cf.vidya.shared.domain.repository.AuthRepository

class SendOtpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(mobile: String) = repository.sendOtp(mobile)
}
