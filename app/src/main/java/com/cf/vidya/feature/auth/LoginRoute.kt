package com.cf.vidya.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cf.vidya.shared.presentation.login.LoginEffect
import com.cf.vidya.shared.presentation.login.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginRoute(
    onOpenOtp: (mobile: String) -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val openOtp by rememberUpdatedState(onOpenOtp)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is LoginEffect.OpenOtp -> openOtp(effect.mobile)
            }
        }
    }

    LoginScreen(state = state, onEvent = viewModel::onEvent)
}
