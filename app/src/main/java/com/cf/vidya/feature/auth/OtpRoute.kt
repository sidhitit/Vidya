package com.cf.vidya.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cf.vidya.shared.presentation.otp.OtpEffect
import com.cf.vidya.shared.presentation.otp.OtpViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun OtpRoute(
    mobile: String,
    onLoggedIn: () -> Unit,
    onBack: () -> Unit,
    viewModel: OtpViewModel = koinViewModel { parametersOf(mobile) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val loggedIn by rememberUpdatedState(onLoggedIn)
    val back by rememberUpdatedState(onBack)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                OtpEffect.OpenHome -> loggedIn()
                OtpEffect.GoBack -> back()
            }
        }
    }

    OtpScreen(state = state, onEvent = viewModel::onEvent)
}
