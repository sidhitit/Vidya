package com.cf.vidya.shared.presentation.login

import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.domain.model.AuthRules

data class LoginState(
    val mobile: String = "",
    val isSending: Boolean = false,
    val error: AppError? = null,
) {
    val isMobileValid: Boolean get() = AuthRules.isValidMobile(mobile)

    /** All 10 digits typed but the number can't be right (e.g. starts with 1). */
    val showInvalidMobile: Boolean get() = mobile.length == AuthRules.MOBILE_LENGTH && !isMobileValid
}

sealed interface LoginEvent {
    data class MobileChanged(val value: String) : LoginEvent
    data object GetOtpClicked : LoginEvent
}

sealed interface LoginEffect {
    data class OpenOtp(val mobile: String) : LoginEffect
}
