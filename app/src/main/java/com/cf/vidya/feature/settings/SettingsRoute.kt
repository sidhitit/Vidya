package com.cf.vidya.feature.settings

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cf.vidya.feature.common.AppLinks
import com.cf.vidya.feature.common.emailSupport
import com.cf.vidya.feature.common.messageRes
import com.cf.vidya.feature.common.openUrl
import com.cf.vidya.feature.common.shareApp
import com.cf.vidya.shared.presentation.settings.SettingsEffect
import com.cf.vidya.shared.presentation.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val back by rememberUpdatedState(onBack)
    val loggedOut by rememberUpdatedState(onLoggedOut)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                SettingsEffect.GoBack -> back()
                SettingsEffect.ShareApp -> context.shareApp()
                SettingsEffect.ContactUs -> context.emailSupport()
                SettingsEffect.OpenTerms -> context.openUrl(AppLinks.TERMS_URL)
                SettingsEffect.OpenPrivacyPolicy -> context.openUrl(AppLinks.PRIVACY_URL)
                SettingsEffect.LoggedOut -> loggedOut()
                is SettingsEffect.ShowError ->
                    Toast.makeText(context, effect.error.messageRes(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    SettingsScreen(state = state, onEvent = viewModel::onEvent)
}
