package com.cf.vidya.shared.presentation.profile

import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.domain.usecase.ProfileOverview

data class ProfileState(
    val isLoading: Boolean = true,
    val error: AppError? = null,
    val overview: ProfileOverview? = null,
)

sealed interface ProfileEvent {
    data object RetryClicked : ProfileEvent
    data object BackClicked : ProfileEvent
    data object CopyIdClicked : ProfileEvent
    data object ManageSubscriptionClicked : ProfileEvent
    data object GetPremiumClicked : ProfileEvent
    data class SeriesClicked(val seriesId: String) : ProfileEvent
}

sealed interface ProfileEffect {
    data object GoBack : ProfileEffect
    data class CopyToClipboard(val text: String) : ProfileEffect
    data object OpenManageSubscription : ProfileEffect
    data object OpenPremium : ProfileEffect
    data class OpenSeries(val seriesId: String) : ProfileEffect
}
