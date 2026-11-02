package com.cf.vidya.shared.presentation.otp

import androidx.lifecycle.viewModelScope
import com.cf.vidya.shared.core.MviViewModel
import com.cf.vidya.shared.core.onFailure
import com.cf.vidya.shared.core.onSuccess
import com.cf.vidya.shared.domain.model.AuthRules
import com.cf.vidya.shared.domain.usecase.SendOtpUseCase
import com.cf.vidya.shared.domain.usecase.VerifyOtpUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OtpViewModel(
    mobile: String,
    private val sendOtp: SendOtpUseCase,
    private val verifyOtp: VerifyOtpUseCase,
) : MviViewModel<OtpState, OtpEvent, OtpEffect>(OtpState(mobile = mobile)) {

    private var timerJob: Job? = null

    init {
        startResendTimer()
    }

    override fun onEvent(event: OtpEvent) {
        when (event) {
            is OtpEvent.OtpChanged -> setState {
                copy(otp = event.value.filter(Char::isDigit).take(AuthRules.OTP_LENGTH), error = null)
            }
            OtpEvent.LoginClicked -> verify()
            OtpEvent.ResendClicked -> resend()
            OtpEvent.EditNumberClicked -> sendEffect(OtpEffect.GoBack)
        }
    }

    private fun verify() {
        val state = currentState
        if (!state.isOtpComplete || state.isVerifying) return
        viewModelScope.launch {
            setState { copy(isVerifying = true, error = null) }
            verifyOtp(state.mobile, state.otp)
                .onSuccess {
                    setState { copy(isVerifying = false) }
                    sendEffect(OtpEffect.OpenHome)
                }
                .onFailure { setState { copy(isVerifying = false, error = it) } }
        }
    }

    private fun resend() {
        if (!currentState.canResend) return
        viewModelScope.launch {
            setState { copy(isResending = true, error = null) }
            sendOtp(currentState.mobile)
                .onSuccess {
                    setState { copy(isResending = false) }
                    startResendTimer()
                }
                .onFailure { setState { copy(isResending = false, error = it) } }
        }
    }

    private fun startResendTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            setState { copy(resendSecondsLeft = AuthRules.RESEND_SECONDS) }
            while (currentState.resendSecondsLeft > 0) {
                delay(1_000)
                setState { copy(resendSecondsLeft = resendSecondsLeft - 1) }
            }
        }
    }
}
