package com.cf.vidya.shared.presentation.premium

import androidx.lifecycle.viewModelScope
import com.cf.vidya.shared.core.MviViewModel
import com.cf.vidya.shared.core.onFailure
import com.cf.vidya.shared.core.onSuccess
import com.cf.vidya.shared.domain.usecase.GetTrialOfferUseCase
import com.cf.vidya.shared.domain.usecase.StartTrialUseCase
import kotlinx.coroutines.launch

class PremiumViewModel(
    private val getTrialOffer: GetTrialOfferUseCase,
    private val startTrial: StartTrialUseCase,
) : MviViewModel<PremiumState, PremiumEvent, PremiumEffect>(PremiumState()) {

    init {
        load()
    }

    override fun onEvent(event: PremiumEvent) {
        when (event) {
            PremiumEvent.RetryClicked -> load()
            PremiumEvent.StartTrialClicked -> startTrial()
        }
    }

    private fun load() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            getTrialOffer()
                .onSuccess { setState { copy(isLoading = false, offer = it) } }
                .onFailure { setState { copy(isLoading = false, error = it) } }
        }
    }

    private fun startTrial() {
        val offer = currentState.offer ?: return
        // Ignore repeat taps so the user can't be charged twice.
        if (currentState.isStartingTrial) return
        setState { copy(isStartingTrial = true) }
        viewModelScope.launch {
            startTrial(offer.id)
                .onSuccess { sendEffect(PremiumEffect.TrialStarted) }
                .onFailure { sendEffect(PremiumEffect.ShowError(it)) }
            setState { copy(isStartingTrial = false) }
        }
    }
}
