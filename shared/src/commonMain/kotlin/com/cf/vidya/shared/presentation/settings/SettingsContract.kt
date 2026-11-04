package com.cf.vidya.shared.presentation.settings

import com.cf.vidya.shared.core.AppError

data class SettingsState(
    /** The "Log out?" dialog is open. */
    val isLogoutDialogVisible: Boolean = false,
    val isLoggingOut: Boolean = false,
)

sealed interface SettingsEvent {
    data object BackClicked : SettingsEvent
    data object ShareAppClicked : SettingsEvent
    data object ContactUsClicked : SettingsEvent
    data object TermsClicked : SettingsEvent
    data object PrivacyPolicyClicked : SettingsEvent
    data object LogoutClicked : SettingsEvent
    data object LogoutConfirmed : SettingsEvent
    data object LogoutDismissed : SettingsEvent
}

sealed interface SettingsEffect {
    data object GoBack : SettingsEffect
    data object ShareApp : SettingsEffect
    data object ContactUs : SettingsEffect
    data object OpenTerms : SettingsEffect
    data object OpenPrivacyPolicy : SettingsEffect
    data object LoggedOut : SettingsEffect
    data class ShowError(val error: AppError) : SettingsEffect
}
