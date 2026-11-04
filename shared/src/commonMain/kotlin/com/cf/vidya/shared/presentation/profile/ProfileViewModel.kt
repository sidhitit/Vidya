package com.cf.vidya.shared.presentation.profile

import androidx.lifecycle.viewModelScope
import com.cf.vidya.shared.core.MviViewModel
import com.cf.vidya.shared.core.onFailure
import com.cf.vidya.shared.core.onSuccess
import com.cf.vidya.shared.domain.usecase.GetProfileOverviewUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getProfileOverview: GetProfileOverviewUseCase,
) : MviViewModel<ProfileState, ProfileEvent, ProfileEffect>(ProfileState()) {

    init {
        load()
    }

    override fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.RetryClicked -> load()
            ProfileEvent.BackClicked -> sendEffect(ProfileEffect.GoBack)
            ProfileEvent.SettingsClicked -> sendEffect(ProfileEffect.OpenSettings)
            ProfileEvent.CopyIdClicked -> currentState.overview?.let {
                sendEffect(ProfileEffect.CopyToClipboard(it.profile.id))
            }
            ProfileEvent.ManageSubscriptionClicked -> sendEffect(ProfileEffect.OpenManageSubscription)
            ProfileEvent.GetPremiumClicked -> sendEffect(ProfileEffect.OpenPremium)
            is ProfileEvent.SeriesClicked -> sendEffect(ProfileEffect.OpenSeries(event.seriesId))
        }
    }

    private fun load() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            getProfileOverview()
                .onSuccess { setState { copy(isLoading = false, overview = it) } }
                .onFailure { setState { copy(isLoading = false, error = it) } }
        }
    }
}
