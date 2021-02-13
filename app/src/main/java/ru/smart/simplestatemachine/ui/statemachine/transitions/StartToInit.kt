package ru.smart.simplestatemachine.ui.statemachine.transitions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.smart.simplestatemachine.ui.statemachine.Event
import ru.smart.simplestatemachine.ui.statemachine.State
import ru.smart.simplestatemachine.ui.statemachine.base.TransitionState
import ru.smart.simplestatemachine.ui.statemachine.base.TransitionTo
import ru.smart.simplestatemachine.ui.statemachine.base.createTransitionStateChanged

class StartToInit(private val viewModelScope: CoroutineScope):
    TransitionTo<State.Start, State.Init, Event.OnInit> {
    override fun execute(
        state: State.Start,
        event: Event.OnInit,
        onState: (TransitionState<State.Init>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000L)
            viewModelScope.launch {
                onState.invoke(createTransitionStateChanged(State.Init()))
            }
        }
    }
}