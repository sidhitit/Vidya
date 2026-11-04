package com.cf.vidya.shared.data.fake

import com.cf.vidya.shared.core.AppResult
import com.cf.vidya.shared.domain.model.TrialOffer
import com.cf.vidya.shared.domain.repository.SubscriptionRepository
import kotlinx.coroutines.delay

/** The ₹1 trial from the design. Starting it always succeeds; no money is taken. */
class FakeSubscriptionRepository : SubscriptionRepository {

    override suspend fun getTrialOffer(): AppResult<TrialOffer> {
        delay(800)
        return AppResult.Success(
            TrialOffer(
                id = "trial_1_day",
                trialPrice = 1,
                originalPrice = 99,
                trialDays = 1,
                renewalPrice = 599,
                renewalMonths = 3,
                seriesCount = 1000,
                previewPosters = SampleData.series.take(5).map { it.posterUrl },
            ),
        )
    }

    override suspend fun startTrial(offerId: String): AppResult<Unit> {
        delay(1200)
        return AppResult.Success(Unit)
    }
}
