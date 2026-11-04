package com.cf.vidya.feature.premium

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cf.vidya.R
import com.cf.vidya.feature.common.messageRes
import com.cf.vidya.shared.presentation.premium.PremiumEffect
import com.cf.vidya.shared.presentation.premium.PremiumViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PremiumRoute(viewModel: PremiumViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            val message = when (effect) {
                PremiumEffect.TrialStarted -> R.string.trial_started
                is PremiumEffect.ShowError -> effect.error.messageRes()
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    PremiumScreen(state = state, onEvent = viewModel::onEvent)
}
