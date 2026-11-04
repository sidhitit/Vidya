package com.cf.vidya.shared.presentation.settings

import androidx.lifecycle.viewModelScope
import com.cf.vidya.shared.core.MviViewModel
import com.cf.vidya.shared.core.onFailure
import com.cf.vidya.shared.core.onSuccess
import com.cf.vidya.shared.domain.usecase.LogoutUseCase
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val logout: LogoutUseCase,
) : MviViewModel<SettingsState, SettingsEvent, SettingsEffect>(SettingsState()) {

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.BackClicked -> sendEffect(SettingsEffect.GoBack)
            SettingsEvent.ShareAppClicked -> sendEffect(SettingsEffect.ShareApp)
            SettingsEvent.ContactUsClicked -> sendEffect(SettingsEffect.ContactUs)
            SettingsEvent.TermsClicked -> sendEffect(SettingsEffect.OpenTerms)
            SettingsEvent.PrivacyPolicyClicked -> sendEffect(SettingsEffect.OpenPrivacyPolicy)
            SettingsEvent.LogoutClicked -> setState { copy(isLogoutDialogVisible = true) }
            SettingsEvent.LogoutDismissed -> setState { copy(isLogoutDialogVisible = false) }
            SettingsEvent.LogoutConfirmed -> logOut()
        }
    }

    private fun logOut() {
        if (currentState.isLoggingOut) return
        setState { copy(isLoggingOut = true) }
        viewModelScope.launch {
            logout()
                .onSuccess { sendEffect(SettingsEffect.LoggedOut) }
                .onFailure { sendEffect(SettingsEffect.ShowError(it)) }
            setState { copy(isLoggingOut = false, isLogoutDialogVisible = false) }
        }
    }
}
