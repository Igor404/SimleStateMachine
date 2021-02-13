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
import ru.smart.simplestatemachine.ui.statemachine.base.createTransitionStateError
import java.io.IOException

class InitToNext(private val viewModelScope: CoroutineScope):
    TransitionTo<State.Init, State.Next, Event.OnNext> {

    var errorCnt = 1

    override fun execute(
        state: State.Init,
        event: Event.OnNext,
        onState: (TransitionState<State.Next>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000L)
            viewModelScope.launch {
                onState.invoke(emulateError())
            }
        }
    }

    private fun emulateError(): TransitionState<State.Next> {
        errorCnt++
        return if (errorCnt % 2 == 0)  {
            createTransitionStateError(IOException("NetworkException"))
        } else {
            createTransitionStateChanged(State.Next())
        }
    }
}