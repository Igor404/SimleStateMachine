package ru.smart.simplestatemachine.ui.view.delegates.base

interface IDelegate<in T: Any, in V: Any> {

    fun onBind(viewState: T)

    fun onUnBind()

    fun isFor(entry: T): Boolean

    fun onVisibility(isVisible: Boolean)

    fun onCreateView(parent: V)
}