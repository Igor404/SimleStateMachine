package ru.smart.simplestatemachine.ui.statemachine.base

interface TransitionTo<in InState : Any, out OutState : Any, in Event : Any> {
    fun execute(
        state: InState,
        event: Event,
        onState: (TransitionState<OutState>) -> Unit
    )
}

interface TransitionHandler<in InState : Any, in OutState : Any> {
    fun execute(
        state: InState,
        transitionState: TransitionState<OutState>
    )
}