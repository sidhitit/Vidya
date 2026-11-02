package com.cf.vidya.shared.presentation.login

import androidx.lifecycle.viewModelScope
import com.cf.vidya.shared.core.MviViewModel
import com.cf.vidya.shared.core.onFailure
import com.cf.vidya.shared.core.onSuccess
import com.cf.vidya.shared.domain.model.AuthRules
import com.cf.vidya.shared.domain.usecase.SendOtpUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val sendOtp: SendOtpUseCase,
) : MviViewModel<LoginState, LoginEvent, LoginEffect>(LoginState()) {

    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.MobileChanged -> setState {
                // Keep digits only, so pasted "+91 98765 43210" style text can't break the field.
                copy(mobile = event.value.filter(Char::isDigit).take(AuthRules.MOBILE_LENGTH), error = null)
            }
            LoginEvent.GetOtpClicked -> requestOtp()
        }
    }

    private fun requestOtp() {
        val state = currentState
        if (!state.isMobileValid || state.isSending) return
        viewModelScope.launch {
            setState { copy(isSending = true, error = null) }
            sendOtp(state.mobile)
                .onSuccess {
                    setState { copy(isSending = false) }
                    sendEffect(LoginEffect.OpenOtp(state.mobile))
                }
                .onFailure { setState { copy(isSending = false, error = it) } }
        }
    }
}
