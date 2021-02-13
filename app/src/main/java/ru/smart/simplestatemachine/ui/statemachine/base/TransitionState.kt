package ru.smart.simplestatemachine.ui.statemachine.base

sealed class TransitionState<out T> {

    data class StateChanged<out T>(val data: T) : TransitionState<T>()

    data class StateError(val data: Throwable) : TransitionState<Nothing>()
}

fun <T> createTransitionStateChanged(data: T) = TransitionState.StateChanged(data)
fun createTransitionStateError(error: Throwable) = TransitionState.StateError(error)

