package com.cf.vidya.shared.presentation.premium

import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.domain.model.TrialOffer

data class PremiumState(
    val isLoading: Boolean = true,
    val error: AppError? = null,
    val offer: TrialOffer? = null,
    /** True while the trial is being started; the button shows a spinner. */
    val isStartingTrial: Boolean = false,
)

sealed interface PremiumEvent {
    data object RetryClicked : PremiumEvent
    data object StartTrialClicked : PremiumEvent
}

sealed interface PremiumEffect {
    data object TrialStarted : PremiumEffect
    data class ShowError(val error: AppError) : PremiumEffect
}
