package com.cf.vidya.shared.domain.usecase

import com.cf.vidya.shared.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}
