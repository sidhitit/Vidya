package com.cf.vidya.shared.presentation.otp

import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.domain.model.AuthRules

data class OtpState(
    val mobile: String,
    val otp: String = "",
    val isVerifying: Boolean = false,
    val isResending: Boolean = false,
    val resendSecondsLeft: Int = AuthRules.RESEND_SECONDS,
    val error: AppError? = null,
) {
    val isOtpComplete: Boolean get() = AuthRules.isValidOtp(otp)
    val canResend: Boolean get() = resendSecondsLeft == 0 && !isResending
}

sealed interface OtpEvent {
    data class OtpChanged(val value: String) : OtpEvent
    data object LoginClicked : OtpEvent
    data object ResendClicked : OtpEvent
    data object EditNumberClicked : OtpEvent
}

sealed interface OtpEffect {
    data object OpenHome : OtpEffect
    data object GoBack : OtpEffect
}
