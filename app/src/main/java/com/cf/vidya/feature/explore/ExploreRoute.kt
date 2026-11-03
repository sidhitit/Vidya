package com.cf.vidya.feature.explore

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cf.vidya.R
import com.cf.vidya.shared.presentation.explore.ExploreEffect
import com.cf.vidya.shared.presentation.explore.ExploreViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExploreRoute(
    onOpenSeries: (String) -> Unit,
    viewModel: ExploreViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val openSeries by rememberUpdatedState(onOpenSeries)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ExploreEffect.OpenSeries -> openSeries(effect.seriesId)
                is ExploreEffect.ShareSeries -> {
                    // TODO: add a link that opens the series in the app, once deep links exist.
                    val send = Intent(Intent.ACTION_SEND)
                        .setType("text/plain")
                        .putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_series_text, effect.seriesTitle))
                    context.startActivity(Intent.createChooser(send, null))
                }
            }
        }
    }

    ExploreScreen(state = state, onEvent = viewModel::onEvent)
}
