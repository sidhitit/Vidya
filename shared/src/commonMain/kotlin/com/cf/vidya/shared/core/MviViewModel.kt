package com.cf.vidya.shared.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base class for every screen's ViewModel (MVI-style MVVM).
 *
 * - [S] State: one immutable data class describing everything the screen shows.
 * - [E] Event: sealed type of everything the user can do; the only way in is [onEvent].
 * - [F] Effect: one-off actions (navigate, snackbar) delivered exactly once via [effects].
 */
abstract class MviViewModel<S, E, F>(initialState: S) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<F>(Channel.BUFFERED)
    val effects: Flow<F> = _effects.receiveAsFlow()

    protected val currentState: S get() = _state.value

    abstract fun onEvent(event: E)

    protected fun setState(reduce: S.() -> S) = _state.update(reduce)

    protected fun sendEffect(effect: F) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
