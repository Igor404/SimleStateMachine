package ru.smart.simplestatemachine.ui.statemachine.base

import android.util.Log
import ru.smart.simplestatemachine.ui.statemachine.State
import java.util.ArrayList
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicReference

typealias ClassPair<T1, T2> = Pair<Class<out T1>, Class<out T2>>
typealias TransitionStateListener<InState, OutState, Event> = (state: InState, event: Event, TransitionState<OutState>) -> Unit

open class StateMachine<State : Any, Event : Any> {

    enum class ExecutingState {
        EXECUTING, EXECUTED
    }

    val transitions: MutableMap<ClassPair<State, Event>, TransitionTo<State, State, Event>> = mutableMapOf()
    val handlers: MutableMap<ClassPair<State, Event>, TransitionHandler<State, State>> = mutableMapOf()
    private val executingStateListeners: ArrayList<(ExecutingState) -> Unit> = arrayListOf()
    private val statesStack = LinkedList<State>()

    private val stateRef = AtomicReference<State>()
    private val onStateListeners: ArrayList<TransitionStateListener<State, State, Event>> = arrayListOf()

    private val state: State
        get() = stateRef.get()

    fun initialState(initial: State): StateMachine<State, Event> {
        stateRef.set(initial)
        statesStack.clear()
        statesStack.add(initial)
        return this
    }

    fun <E : Event> transitionTo(event: E) {
        transitionTo(state, event)
    }

    private fun <E : Event> transitionTo(state: State, event: E) {
        val transition = transitions[Pair(state::class.java, event::class.java)]
        val handler = handlers[Pair(state::class.java, event::class.java)]

        transition?.let {
            notifyExecutingStateChanged(ExecutingState.EXECUTING)

            it.execute(state, event, { transitionState ->
                notifyExecutingStateChanged(ExecutingState.EXECUTED)

                if (transitionState is TransitionState.StateChanged) {
                    val newState = transitionState.data

                    stateRef.set(newState)
                    statesStack.add(newState)
                }

                handler?.let {
                    handler.execute(state, transitionState)
                }

                notifyOnTransitionStateChanged(state, event, transitionState)

            })
        } ?: Log.d(TAG, "Not found transition for event '${event::class.java}' and state '${state::class.java}'")
    }

    private fun notifyOnTransitionStateChanged(
        state: State,
        event: Event,
        transitionState: TransitionState<State>
    ) {
        onStateListeners.forEach { it.invoke(state, event, transitionState) }
    }

    fun addExecutingStateListener(listener: (ExecutingState) -> Unit) {
        executingStateListeners.add(listener)
    }

    private fun notifyExecutingStateChanged(executingState: ExecutingState) {
        executingStateListeners.forEach { it.invoke(executingState) }
    }

    private companion object {
        private const val TAG = "StateMachine"
    }

}

fun <S: State, SO: State> createHandler(onState: (S, TransitionState<SO>) -> Unit): TransitionHandler<S, SO> {
    return object: TransitionHandler<S, SO> {
        override fun execute(state: S, transitionState: TransitionState<SO>) {
            onState.invoke(state, transitionState)
        }
    }
}