package com.cf.vidya.feature.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cf.vidya.shared.presentation.library.LibraryEffect
import com.cf.vidya.shared.presentation.library.LibraryViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryRoute(
    onOpenSeries: (String) -> Unit,
    onOpenExplore: () -> Unit,
    viewModel: LibraryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val openSeries by rememberUpdatedState(onOpenSeries)
    val openExplore by rememberUpdatedState(onOpenExplore)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is LibraryEffect.OpenSeries -> openSeries(effect.seriesId)
                LibraryEffect.OpenExplore -> openExplore()
            }
        }
    }

    LibraryScreen(state = state, onEvent = viewModel::onEvent)
}
