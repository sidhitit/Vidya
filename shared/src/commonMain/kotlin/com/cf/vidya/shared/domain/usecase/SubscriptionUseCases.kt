package com.cf.vidya.shared.domain.usecase

import com.cf.vidya.shared.domain.repository.SubscriptionRepository

class GetTrialOfferUseCase(private val repository: SubscriptionRepository) {
    suspend operator fun invoke() = repository.getTrialOffer()
}

class StartTrialUseCase(private val repository: SubscriptionRepository) {
    suspend operator fun invoke(offerId: String) = repository.startTrial(offerId)
}
