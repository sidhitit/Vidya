package com.cf.vidya.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cf.vidya.shared.presentation.home.HomeEffect
import com.cf.vidya.shared.presentation.home.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

/** Connects [HomeViewModel] to [HomeScreen] and turns effects into navigation. */
@Composable
fun HomeRoute(
    onOpenSeries: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val openSeries by rememberUpdatedState(onOpenSeries)
    val openSearch by rememberUpdatedState(onOpenSearch)
    val openProfile by rememberUpdatedState(onOpenProfile)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.OpenSeries -> openSeries(effect.seriesId)
                HomeEffect.OpenSearch -> openSearch()
                HomeEffect.OpenProfile -> openProfile()
            }
        }
    }

    HomeScreen(state = state, onEvent = viewModel::onEvent)
}
