package com.cf.vidya.shared.domain.repository

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.TrialOffer

interface SubscriptionRepository {
    suspend fun getTrialOffer(): AppResult<TrialOffer>

    // TODO: the real implementation goes through Google Play Billing (backend phase).
    suspend fun startTrial(offerId: String): AppResult<Unit>
}
