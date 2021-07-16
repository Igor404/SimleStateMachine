package ru.smart.simplestatemachine.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.smart.simplestatemachine.ui.statemachine.Event
import ru.smart.simplestatemachine.ui.statemachine.State
import ru.smart.simplestatemachine.ui.statemachine.ViewState
import ru.smart.simplestatemachine.ui.statemachine.base.TransitionHandler
import ru.smart.simplestatemachine.ui.statemachine.base.TransitionState
import ru.smart.simplestatemachine.ui.statemachine.base.TransitionTo
import ru.smart.simplestatemachine.ui.statemachine.base.ClassPair
import ru.smart.simplestatemachine.ui.statemachine.base.StateMachine
import ru.smart.simplestatemachine.ui.statemachine.base.createHandler
import ru.smart.simplestatemachine.ui.statemachine.transitions.InitToNext
import ru.smart.simplestatemachine.ui.statemachine.transitions.StartToInit
import ru.smart.simplestatemachine.ui.statemachine.transitions.StartToNext
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(
    private val stateMachine: StateMachine<State, Event>
): ViewModel() {

    val viewState: LiveData<ViewState> = MutableLiveData()
    val loadingState: LiveData<Boolean> = MutableLiveData()
    val errorState: LiveData<ViewState.Error> = MutableLiveData()

    private val transitionChangeErrorHandler: (TransitionState.StateError) -> Unit = {
        errorState.post(ViewState.Error(it.data.localizedMessage.orEmpty()))
    }

    private val startToInitHandler = handle<State.Start, State.Init>(transitionChangeErrorHandler) { s, ts ->
        ViewState.InitViewState("init") {
            sendEvent(Event.OnNext())
        }
    }

    private val startToNextHandler = handle<State.Start, State.Next>(transitionChangeErrorHandler) { s, ts ->
        ViewState.NextViewState("next") {
            sendEvent(Event.OnNext())
        }
    }

    private val initToNextHandler = handle<State.Init, State.Next>(transitionChangeErrorHandler) { s, ts ->
        ViewState.NextViewState("next") {
            restartStateMachine()
        }
    }

    init {
        stateMachine.initialState(State.Start())

        stateMachine.addExecutingStateListener { loadingState.post(it == StateMachine.ExecutingState.EXECUTING) }

        stateBuilder<State.Start> {
            this.next(StartToInit(viewModelScope), startToInitHandler) {
                this.next(InitToNext(viewModelScope), initToNextHandler) {}
            }
            this.next(StartToNext(viewModelScope), startToNextHandler) {}
        }.build(stateMachine)

        sendEvent(Event.OnInit())
    }

    private fun restartStateMachine() {
        stateMachine.initialState(State.Start())
        sendEvent(Event.OnInit())
    }

    private fun sendEvent(event: Event) {
        stateMachine.transitionTo(event)
    }

    private fun postViewState(vs: ViewState) {
        viewState.post(vs)
    }

    private fun <T> LiveData<T>.post(value: T) {
        (this as MutableLiveData<T>).postValue(value)
    }

    private fun <S: State, SO: State> handle(errorHandler: (TransitionState.StateError) -> Unit, onState: (S, TransitionState<SO>) -> ViewState): TransitionHandler<S, SO> {
        return createHandler { s, ts ->
            when (ts) {
                is TransitionState.StateError -> errorHandler.invoke(ts)
                else -> postViewState(onState.invoke(s, ts))
            }
        }
    }
}

class StatesBuilder {

    val transitions: MutableMap<ClassPair<State, Event>, TransitionTo<State, State, Event>> =
        mutableMapOf()
    val handlers: MutableMap<ClassPair<State, Event>, TransitionHandler<State, State>> = mutableMapOf()

    inner class TransitionBuilder<InS : State> {

        inline fun <reified IS : InS, reified NextState : State, reified E : Event> next(
            transitionTo: TransitionTo<InS, NextState, E>,
            transitionHandler: TransitionHandler<IS, NextState>? = null,
            initializer: TransitionBuilder<NextState>.() -> Unit
        ) {
            @Suppress("UNCHECKED_CAST")
            transitions[Pair(IS::class.java, E::class.java)] =
                transitionTo as TransitionTo<State, State, Event>
            @Suppress("UNCHECKED_CAST")
            transitionHandler?.let {
                handlers[Pair(IS::class.java, E::class.java)] = transitionHandler as TransitionHandler<State, State>
            }
            TransitionBuilder<NextState>().apply(initializer)
        }

    }

    fun build(stateMachine: StateMachine<State, Event>) {
        stateMachine.transitions.putAll(transitions)
        stateMachine.handlers.putAll(handlers)
    }

}

fun <InS: State> stateBuilder(initializer: StatesBuilder.TransitionBuilder<InS>.() -> Unit): StatesBuilder {
    val b = StatesBuilder()
    b.TransitionBuilder<InS>().apply(initializer)
    return b
}
