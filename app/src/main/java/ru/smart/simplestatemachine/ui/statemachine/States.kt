package ru.smart.simplestatemachine.ui.statemachine

open class Event {
    class OnInit() : Event()
    class OnNext() : Event()
}

sealed class ViewState {
    data class InitViewState(val label: String, val nextCallback: () -> Unit): ViewState()
    data class NextViewState(val label: String, val nextCallback: () -> Unit): ViewState()
    data class Error(val text: String): ViewState()
}

open class State {
    class Start : State()
    class Init() : State()
    class Next() : State()
}


